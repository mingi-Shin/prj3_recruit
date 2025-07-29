package kr.co.sist.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.sist.user.dto.UserDTO;

/**
 * if(로그인 성공){
 * 	JWTFilter역할 = 쿠키 검증 -> 세션생성 -> 세션에 유저정보 저장 
 * }
 * SecurityContextholder에 세션을 생성하는데, 이 세션은 Stateless상태로 관리되기 때문에 해당 요청이 끝나면 소멸됨.
 */

public class JWTFIlter extends OncePerRequestFilter{

	private final JWTUtil jwtUtil;
	
	public JWTFIlter(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String authorization = null;
		Cookie[] cookies = request.getCookies();
		if(cookies == null || cookies.length == 0) {
			filterChain.doFilter(request, response);
			return;
		}
		 
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("Authorization")) {
				authorization = cookie.getValue();
			}
		}
		String accessToken = authorization; //사용자 정보 보유중 
		
		/**
		 * 07.23 추가(access token 검증)
		 */
		//String accessToken = request.getHeader("access"); // != getCookies() 주의
		
		//accessToken 없으면 다음필터로 
		if(accessToken == null) {
			filterChain.doFilter(request, response);
			return;
		}
		
		//accessToken 만료시간 체크
		try {
			jwtUtil.isExpired(accessToken);
		} catch (ExpiredJwtException e) {
			e.printStackTrace();
			
			//response Body
			PrintWriter writer = response.getWriter();
			writer.print("access token expired!");
		
			//response status code
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		//토큰이 access인지 확인(발급시에 페이로드에 명시)
		String category = jwtUtil.getCategory(accessToken);
		
		System.out.println("JWTFilter 디버깅 : " + category);

		if(!category.equals("tempJwt")) { 
			
			//response Body
			PrintWriter writer = response.getWriter();
			writer.print("invalid access token.. ");
			
			//response status code
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		//accessToken이 정상이니 SecurityContextHolder세션에 저장하자, principal에 들어갈 것들: (CustomUser 클래스 생성 -> Authentication 인터페이스의 구현체인 UsernamePasswordAuthenticationToken 객체로 생성 )
		UserDTO uDTO = new UserDTO();
		uDTO.setEmail(jwtUtil.getEmail(accessToken));
		uDTO.setName(jwtUtil.getName(accessToken));
		uDTO.setCorpNo(jwtUtil.getCorpNo(accessToken));
		uDTO.setRole(jwtUtil.getRole(accessToken));
		
		//UserDetails 구현체(CustomUser)에 로그인한 회원정보 객체 담기
		CustomUser customUser = new CustomUser(uDTO);
		
		//스프링 시큐리티 인증 토큰 생성 (사용자객체, 비밀번호, 권한)
		Authentication authToken = new UsernamePasswordAuthenticationToken(customUser, null, customUser.getAuthorities());

		//시큐리티 세션에 저장 (stateless 동작)
		SecurityContextHolder.getContext().setAuthentication(authToken);
		
		//끝. 다음 필터로 이동
		filterChain.doFilter(request, response);

		
	}
	
}
