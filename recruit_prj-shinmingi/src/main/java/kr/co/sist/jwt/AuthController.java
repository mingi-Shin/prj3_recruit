package kr.co.sist.jwt;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.sist.jwt.JWTUtil;
import kr.co.sist.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 서비스가 없어서 컨트로러가 너무 비대하긴 한데, 일단 만들어놓자 
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JWTUtil jwtUtil;

    /**
     * Access Token 재발급
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue(value = "refresh", required = false) String refresh,
                                   HttpServletResponse response) {
        
        log.info("=== 토큰 재발급 요청 ===");
        
        // Refresh Token 검증
        if (refresh == null || refresh.trim().isEmpty()) {
            log.warn("Refresh Token이 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Refresh Token이 없습니다. 다시 로그인해주세요."));
        }

        try {
            // 토큰 만료 검증
            jwtUtil.isExpired(refresh);
            
            // 토큰 타입 검증
            String category = jwtUtil.getCategory(refresh);
            if (!"refresh".equals(category)) {
                log.warn("잘못된 토큰 타입: {}", category);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "잘못된 토큰입니다."));
            }

            // 사용자 정보 추출
            String email = jwtUtil.getEmail(refresh);
            String role = jwtUtil.getRole(refresh);
            
            log.info("토큰 재발급 - 사용자: {}, 권한: {}", email, role);
            
            // UserDTO 생성 (실제로는 DB에서 조회해야 할 수도 있음)
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(email);
            userDTO.setRole(role);
            
            // 새로운 토큰 생성
            String newAccessToken = jwtUtil.createJwt("access", userDTO, 10 * 60 * 1000L); // 10분
            String newRefreshToken = jwtUtil.createJwt("refresh", userDTO, 24 * 60 * 60 * 1000L); // 24시간
            
            // Access Token을 응답 헤더에 설정
            response.setHeader("Authorization", "Bearer " + newAccessToken);
            
            // 새로운 Refresh Token을 쿠키로 설정
            ResponseCookie refreshCookie = ResponseCookie.from("refresh", newRefreshToken)
                    .httpOnly(true)
                    .secure(false) // 개발환경: false, 운영환경: true
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(Duration.ofHours(24))
                    .build();
            
            response.addHeader("Set-Cookie", refreshCookie.toString());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "토큰이 재발급되었습니다.",
                "accessToken", newAccessToken
            ));
            
        } catch (ExpiredJwtException e) {
            log.warn("만료된 Refresh Token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "토큰이 만료되었습니다. 다시 로그인해주세요."));
                    
        } catch (Exception e) {
            log.error("토큰 재발급 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "토큰 재발급에 실패했습니다."));
        }
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        
        log.info("=== 로그아웃 요청 ===");
        
        // Refresh Token 쿠키 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refresh", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        
        response.addHeader("Set-Cookie", deleteCookie.toString());
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "로그아웃 되었습니다."
        ));
    }
}