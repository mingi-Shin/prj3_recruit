package kr.co.sist.jwt;

import java.io.IOException;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		Throwable cause = exception.getCause();
		
		String errorMsg = "알 수 없는 로그인 오류가 발생하였습니다.";
		
		//UserDetailsService 안에서 발생하는 예외는
		//Spring Security 내부적으로 InternalAuthenticationServiceException으로 감싸서 던집니다.
		
		if(cause instanceof UsernameNotFoundException) { //스프링 시큐리티 보안정책으로 인해 UsernameNotFoundException는 BadCredentialsException로 덮어씌워짐
			errorMsg = "등록되지 않은 이메일입니다.";
		} else if (exception instanceof BadCredentialsException) { //BadCredentialsException 은 getCause()값이 null이다.
			errorMsg = "이메일 또는 비밀번호가 일치하지 않습니다.";
		} else if (cause instanceof LockedException) {
			errorMsg = "계정이 잠겨 있습니다.";
		} else if (cause instanceof DisabledException) {
			errorMsg = "운영수칙을 위반하여 계정이 비활성화되어있습니다. 고객센터에 문의해주세요.";
		} else if (cause instanceof AccountExpiredException) {
			errorMsg = "회원 탈퇴한 계정입니다.";
		} else if (cause instanceof CredentialsExpiredException) {
			errorMsg = "비밀번호 유효기간이 만료되었습니다.";
		}
		
		// 메시지를 쿼리파라미터로 전달 (GET 방식으로 리다이렉트)
		//이게 사용자에게 전달됨. 
		response.sendRedirect("/login?error=true&errorMessage=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
		 
	}

}
