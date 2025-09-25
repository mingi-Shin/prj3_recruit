package kr.co.sist.jwt;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NewLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public NewLoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.objectMapper = new ObjectMapper();
        
        // 로그인 처리 URL 설정 1,2
        //setFilterProcessesUrl("/api/auth/login"); //1. post,get 모두 가능 
        
        setRequiresAuthenticationRequestMatcher(
            new org.springframework.security.web.util.matcher.AntPathRequestMatcher("/api/auth/login", "POST")
        ); // POST만 가능 
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        
        log.info("=== JWT 로그인 시도 ===");
        
        // Content-Type 검증
        String contentType = request.getContentType();
        if (contentType == null || !contentType.contains("application/json")) {
            log.error("잘못된 Content-Type: {}", contentType);
            throw new BadCredentialsException("JSON 형식의 요청만 허용됩니다.");
        }
        
        try {
            // JSON 파싱
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            
            if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                throw new BadCredentialsException("이메일과 비밀번호는 필수입니다.");
            }
            
            log.info("로그인 시도 - 이메일: {}", loginRequest.getEmail());
            
            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), 
                    loginRequest.getPassword()
                );
            
            // AuthenticationManager로 인증 처리
            return authenticationManager.authenticate(authToken);
            
        } catch (IOException e) {
            log.error("JSON 파싱 오류", e);
            throw new BadCredentialsException("요청 데이터 파싱에 실패했습니다.");
        } catch (AuthenticationException e) {
            log.error("인증 실패", e);
            throw e;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        
        log.info("=== 로그인 성공 ===");
        
        try {
            // 사용자 정보 추출
            CustomUser customUser = (CustomUser) authResult.getPrincipal();
            log.info("로그인 성공 사용자: {}", customUser.getUsername());
            
            // JWT 토큰 생성
            String accessToken = jwtUtil.createJwt("access", customUser.getUserDTO(), 10 * 60 * 1000L); // 10분
            String refreshToken = jwtUtil.createJwt("refresh", customUser.getUserDTO(), 24 * 60 * 60 * 1000L); // 24시간
            
            // Access Token을 응답 헤더에 설정
            response.setHeader("Authorization", "Bearer " + accessToken);
            
            /**
             * 로그인 필터에서만 임시로 access 쿠키 저장 (0914) 
             */
            ResponseCookie accessCookie = createAccessCookie(accessToken); //바로 삭제할 것 
            response.addHeader("Set-Cookie", accessCookie.toString());
            
            // Refresh Token을 HttpOnly 쿠키로 설정
            ResponseCookie refreshCookie = createRefreshCookie(refreshToken);
            response.addHeader("Set-Cookie", refreshCookie.toString());
            
            // 리다이렉트 URL 결정
            String redirectUrl = determineRedirectUrl(customUser);
            
            // 성공 응답 생성
            LoginResponse loginResponse = new LoginResponse(
                true,
                "로그인 성공",
                accessToken,
                redirectUrl,
                customUser.getUserDTO().getRole()
            );
            
            // JSON 응답 설정
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpStatus.OK.value());
            
            // 응답 전송, 자바 -> json  
            objectMapper.writeValue(response.getWriter(), loginResponse); 
            
        } catch (Exception e) {
            log.error("로그인 성공 처리 중 오류", e);
            sendErrorResponse(response, "로그인 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        
        log.error("=== 로그인 실패 ===", failed);
        
        /**
         * 여기도 로그인 정보에 따른 예외처리 수정해야함 
         */
        String errorMessage;
        if (failed instanceof BadCredentialsException) {
            errorMessage = "이메일 또는 비밀번호가 올바르지 않습니다. (아마 비밀번호 문제임)";
        } else if (failed instanceof InternalAuthenticationServiceException) {
            errorMessage = "사용자 정보를 찾을 수 없습니다.(아이디 존재하지 않음)";
        } else {
            errorMessage = "로그인에 실패했습니다.";
        }
        
        sendErrorResponse(response, errorMessage, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * 에러 응답 전송
     */
    private void sendErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        LoginResponse errorResponse = new LoginResponse(false, message, null, null, null);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status.value());
        
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
    
    /**
     * access Token용 쿠키 생성
     */
    private ResponseCookie createAccessCookie(String accessToken) {
        return ResponseCookie.from("access", accessToken)
                .httpOnly(false) //js에서 접근 불가 
                .secure(false) // 개발환경: false, 운영환경: true
                .sameSite("Lax") // CSRF 방지
                .path("/")
                .maxAge(Duration.ofMinutes(10))
                .build();
    }
    /**
     * Refresh Token용 HttpOnly 쿠키 생성
     */
    private ResponseCookie createRefreshCookie(String refreshToken) {
    	return ResponseCookie.from("refresh", refreshToken)
    			.httpOnly(true) //js에서 접근 불가 
    			.secure(false) // 개발환경: false, 운영환경: true
    			.sameSite("Lax") // CSRF 방지
    			.path("/")
    			.maxAge(Duration.ofHours(24))
    			.build();
    }
    
    /**
     * 사용자 권한에 따른 리다이렉트 URL 결정
     */
    private String determineRedirectUrl(CustomUser customUser) {
        String role = customUser.getUserDTO().getRole();
        
        switch (role) {
            case "ROLE_CORP":
                return "/corp/main";
            case "ROLE_USER":
                return "/";
            default:
                return "/";
        }
    }
    
    /**
     * 로그인 요청 DTO
     */
    public static class LoginRequest {
        private String email;
        private String password;
        
        public LoginRequest() {}
        
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    /**
     * 로그인 응답 DTO
     */
    public static class LoginResponse {
        private boolean success;
        private String message;
        private String accessToken;
        private String redirectUrl;
        private String role;
        
        public LoginResponse() {}
        
        public LoginResponse(boolean success, String message, String accessToken, String redirectUrl, String role) {
            this.success = success;
            this.message = message;
            this.accessToken = accessToken;
            this.redirectUrl = redirectUrl;
            this.role = role;
        }
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getRedirectUrl() { return redirectUrl; }
        public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}