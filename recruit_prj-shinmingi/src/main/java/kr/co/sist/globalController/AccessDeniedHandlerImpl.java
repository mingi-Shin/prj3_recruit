package kr.co.sist.globalController;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		
		String requestUri = request.getRequestURI();
		
		System.out.println("[디버깅] AccessDeniedHandlerImpl / requestUri : " + requestUri);
		
		if(requestUri.contains("/corp")) {
			request.setAttribute("msg", "기업 로그인이 필요한 서비스입니다.");
			request.setAttribute("nextPage", "/");
			request.getRequestDispatcher("/error/redirect").forward(request, response);
		} else {
			request.setAttribute("msg", "로그인이 필요한 서비스입니다.");
			request.setAttribute("nextPage", "/");
			request.getRequestDispatcher("/error/redirect").forward(request, response);
		}
		
	}

}
