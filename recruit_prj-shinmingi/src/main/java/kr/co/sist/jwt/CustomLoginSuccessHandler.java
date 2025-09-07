package kr.co.sist.jwt;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * SecurityConfig의 로그인
 */
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

	private final JWTUtil jwtUtil;
	
	public CustomLoginSuccessHandler(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
	/**
	 * 유저 정보 	
	 */
		CustomUser customUser = (CustomUser) authentication.getPrincipal();
		
	/**
	 * 토큰 생성 (매개변수 : 카테고리(access, refresh), 유저정보, 만료시간) 
	 */
		//String tempJwt = jwtUtil.createJwt("tempJwt", customUser.getUserDTO(), 60 * 60 * 1000L); //리팩토링 할 때 access & refresh로 바꾸기  
    String accessToken = jwtUtil.createJwt("access", customUser.getUserDTO(), 60 * 10 * 1000L); //10분
    String refreshToken = jwtUtil.createJwt("refresh", customUser.getUserDTO(), 24 * 60 * 60 * 1000L); //24시간 
    
  /**
   * 생성된 토큰을 분배 : access -> header (프론트에서 로컬 스토리지에 저장하게됨), refresh -> cookie
   * setHeader("Set-Cookie")는 덮어써서 하나만 되니까, addHeader 메서드를 써야해, 
   * 브라우저가 응답을 받을 때, Set-Cookie 헤더를 자동으로 파싱해서 내부 쿠키 저장소에 저장하는 거예요.
   * 다른 이름의 헤더는 쿠키로 가지 않아  
   */
    // access는 Bearer 붙여서 Authorization 이름의 Header로 
    response.setHeader("Authorization", "Bearer " + accessToken);
    
    // refresh는 Set-Cookie 붙여서 쿠키로 가게끔 
    ResponseCookie responseCookie = createCookie(refreshToken); //refreshJwt 매개변수로 refresh 쿠키 생성 
    response.setHeader("Set-Cookie", responseCookie.toString()); // "Set-Cookie"는 서버 → 클라이언트로 쿠키를 보내기 위한 고정된 HTTP 응답 헤더
    
    response.setStatus(HttpStatus.OK.value()); // 200, 그냥 코드 가독성을 위해 “성공임을 명확히 표시” 하려고 넣은 것
    
    //기업회원 로그인 성공시, 기업메인페이지로 이동  
    boolean hasRoleCorp = customUser.getAuthorities().stream()
    												.anyMatch(auth -> auth.getAuthority().equals("ROLE_CORP"));
    //일반회원 로그인 성공시, 유저메인페이지로 이동 
    boolean hasRoleUser = customUser.getAuthorities().stream()
    												.anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
    //원하는 대로 리다이렉트 
    /**
     * 주의! SecurityContextHolder는 기본적으로 한개의 HTTP요청(Request)동안만 유지 
     * 로그인 성공 후 sendRedirect()를 사용하면, 서버가 302응답을 클라이언트에게 보내고 
     * 브라우저가 새 요청(GET)을 만들어 서버에 요청 -> 새 요청이므로 JWTFilter를 또 가는데, 
     * access토큰정보가 없어서 SecurityContextHolder에 정보가 저장안됨. 따라서 ${user}가 null.
     * 
     *  과거에는 쿠키라서 가져올수 있었는데, header에 넣고나서는 redirect되면 초기화돼서.. 그럼결국 setHeader는 의미가 없네 
     */
    String targetUrl = "/";
    if(hasRoleCorp) {
    	targetUrl = "/corp/main";
    } 
    if(hasRoleUser) {
    	targetUrl = "/";
    }
    //response.sendRedirect(targetUrl);
    
    request.getRequestDispatcher(targetUrl).forward(request, response);
    
	}
	
	//ResponseCookie : 스프링부트의 쿠키 생성클래스
	private ResponseCookie createCookie(String refreshJwt) { 
		
		ResponseCookie cookie = ResponseCookie.from("refresh", refreshJwt)  
														.httpOnly(true) //js접근불가 ('document.cookie' 불가하게됨) -> xss공격을 방어 
														.secure(false) //HTTPS에서만 동작 (개발시 false)
														.sameSite("Strict") //CSRF방지 
														.path("/") //전체경로에 대해 쿠키 전송 
														.maxAge(Duration.ofHours(24)) //쿠키 24시간 유지 (JWT보다 길어야 겠지 )
														.build();
		return cookie;
	}

}

/**
 * 	쿠키(Set-Cookie)는 자동 저장 및 전송되지만
 * 	커스텀 헤더(access, Authorization 등)는 자동 전송되지 않음.
 * 	따라서 서버에서 준 access토큰을 따로 저장해줘야함 
 */
