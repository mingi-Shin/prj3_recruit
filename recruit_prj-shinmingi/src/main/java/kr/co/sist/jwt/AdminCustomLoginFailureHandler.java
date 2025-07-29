package kr.co.sist.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.sist.admin.AdminRepository;
import kr.co.sist.admin.login.log.AdminLoginLogService;

@Component
public class AdminCustomLoginFailureHandler implements AuthenticationFailureHandler {

	 @Autowired
   private AdminLoginLogService adminLoginLogService;
	 
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		
		 String adminId = request.getParameter("admin_email");
     String ip = getClientIP(request);
     String userAgent = request.getHeader("User-Agent");
		
		String errorMsg = "알 수 없는 로그인 오류가 발생하였습니다.";
		
		if(exception instanceof UsernameNotFoundException) { //스프링 시큐리티 보안정책으로 인해 UsernameNotFoundException는 BadCredentialsException로 덮어씌워짐
			errorMsg = "등록되지 않은 이메일입니다.";
		} else if (exception instanceof BadCredentialsException) {
			errorMsg = "이메일 또는 비밀번호가 일치하지 않습니다.";
		} else if (exception.getCause() instanceof LockedException) {
			errorMsg = exception.getMessage(); // "탈퇴한 사용자입니다." 그대로 사용
		} else if (exception.getCause() instanceof DisabledException) {
			errorMsg = exception.getMessage(); // "탈퇴한 사용자입니다." 그대로 사용
		} else if (exception instanceof AccountExpiredException) {
			errorMsg = "계정이 만료되었습니다.";
		}
		
		

		adminLoginLogService.logLogin(adminId, false, ip, userAgent, errorMsg);
		// 메시지를 쿼리파라미터로 전달 (GET 방식으로 리다이렉트)
		response.sendRedirect("/admin/admin_login?error=true&errorMessage=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
		 
	}
	
	 private String getClientIP(HttpServletRequest request) {
     String xfHeader = request.getHeader("X-Forwarded-For");
     return (xfHeader == null) ? request.getRemoteAddr() : xfHeader.split(",")[0];
 }

}
