package kr.co.sist.jwt;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RESTful API 방식의 로그인만을 위한 커스텀 UsernamePasswordAuthenticationFilter
 * formLogin 대신 JSON 형태의 로그인 요청을 처리하고 JWT 토큰을 응답으로 전달
 */

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private final JWTUtil jwtUtil;
	private final ObjectMapper objectMapper;
	
	public CustomUsernamePasswordAuthenticationFilter(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
		this.objectMapper = new ObjectMapper();
		
    // 로그인 엔드포인트 설정 (기본값: /login), 컨트롤러에 따로 로직 필요 없음 
		setFilterProcessesUrl("/api/auth/login");
	}
	
  /**
   * 로그인 시도 메소드
   * JSON 형태의 로그인 요청을 파싱하여 인증 토큰 생성
   */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
    try {
      // JSON 요청 본문에서 로그인 정보 추출
      LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
      
      String email = loginRequest.getEmail();
      String password = loginRequest.getPassword();
      
      // 추출한 정보로 인증 토큰 생성
      UsernamePasswordAuthenticationToken authToken = 
          new UsernamePasswordAuthenticationToken(email, password);
      
      // AuthenticationManager에게 인증 위임
      return getAuthenticationManager().authenticate(authToken);
	      
	  } catch (IOException e) {
	      throw new RuntimeException("로그인 요청 파싱 실패", e);
	  }

	}
	
  /**
   * 로그인 성공 처리 메소드
   * JWT 토큰 생성하여 응답으로 전달 (sendRedirect 사용하지 않음)
   */
	@Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
          FilterChain chain, Authentication authResult) throws IOException, ServletException {
      
      // 인증 성공한 사용자 정보 추출
      CustomUser customUser = (CustomUser) authResult.getPrincipal();
      
      // JWT 토큰 생성
      String accessToken = jwtUtil.createJwt("access", customUser.getUserDTO(), 60 * 10 * 1000L); // 10분
      String refreshToken = jwtUtil.createJwt("refresh", customUser.getUserDTO(), 24 * 60 * 60 * 1000L); // 24시간
      
      // Access Token은 응답 헤더에 설정
      response.setHeader("Authorization", "Bearer " + accessToken);
      
      // Refresh Token은 HttpOnly 쿠키로 설정
      ResponseCookie refreshCookie = createRefreshCookie(refreshToken);
      response.addHeader("Set-Cookie", refreshCookie.toString());
      
      // 사용자 권한에 따른 리다이렉트 URL 결정
      String redirectUrl = determineRedirectUrl(customUser);
      
      // JSON 응답 생성 및 전송
      LoginResponse loginResponse = new LoginResponse(
          true,
          "로그인 성공",
          accessToken,
          redirectUrl,
          customUser.getUserDTO().getRole()
      );
      
      // 응답 헤더 설정
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.setStatus(HttpStatus.OK.value());
      
      // JSON 응답 전송
      objectMapper.writeValue(response.getWriter(), loginResponse);
  }
	
	
	
  /**
   * 로그인 실패 처리 메소드
   * 실패 시 JSON 형태의 에러 응답 전달
   */
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
          AuthenticationException failed) throws IOException, ServletException {
      
      // 에러 응답 생성
      LoginResponse errorResponse = new LoginResponse(
          false,
          "로그인 실패: " + failed.getMessage(),
          null,
          null,
          null
      );
      
      // 응답 헤더 설정
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      
      // JSON 에러 응답 전송
      objectMapper.writeValue(response.getWriter(), errorResponse);
  }
	
	
  /**
   * Refresh Token용 HttpOnly 쿠키 생성
   * XSS 공격 방지를 위해 HttpOnly 설정
   */
  private ResponseCookie createRefreshCookie(String refreshToken) {
    return ResponseCookie.from("refresh", refreshToken)
            .httpOnly(true)          // JavaScript 접근 불가 (XSS 공격 방어)
            .secure(false)           // HTTPS에서만 동작 (개발 시 false, 운영 시 true)
            .sameSite("Strict")      // CSRF 공격 방지
            .path("/")               // 전체 경로에서 쿠키 전송
            .maxAge(Duration.ofHours(24))  // 24시간 유지
            .build();
  }
	
  /**
   * 사용자 권한에 따른 리다이렉트 URL 결정
   */
  private String determineRedirectUrl(CustomUser customUser) {
    boolean hasRoleCorp = customUser.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_CORP"));
    
    boolean hasRoleUser = customUser.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
    
    if (hasRoleCorp) {
        return "/corp/main";
    } else if (hasRoleUser) {
        return "/";
    }
    return "/";
}
	
	
  /**
   * 로그인 요청 DTO (따로 만들어도 되고, 여기다 넣어도 되고)
   * JSON 요청 본문을 매핑하기 위한 클래스
   */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
  public static class LoginRequest {
    private String email;
    private String password;
  }
	
  /**
   * 로그인 응답 DTO (따로 만들어도 되고, 여기다 넣어도 되고)
   * 로그인 성공/실패 결과를 JSON으로 전달하기 위한 클래스
   */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
  public static class LoginResponse {
    private boolean success;
    private String message;
    private String accessToken;
    private String redirectUrl;
    private String role;
  }
	
	
	
}
