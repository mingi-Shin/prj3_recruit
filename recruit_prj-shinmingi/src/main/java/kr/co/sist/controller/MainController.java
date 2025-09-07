package kr.co.sist.controller;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.sist.jwt.CustomUser;
import kr.co.sist.jwt.JWTUtil;
import kr.co.sist.login.UserRepository;
import kr.co.sist.user.dto.UserDTO;
import kr.co.sist.user.entity.UserEntity;
import kr.co.sist.util.CipherUtil;

@Controller
public class MainController {
	
	private final CipherUtil cu;

	private final JWTUtil jwtUtil;
	
	private final UserRepository ur;
	
	public MainController(JWTUtil jwtUtil, UserRepository ur, CipherUtil cipherUtil, CipherUtil cu) {
		this.jwtUtil = jwtUtil;
		this.ur = ur;
		this.cu = cu;
	}
	
	@GetMapping("/")
	public String mainPage(HttpServletRequest request) {
		
		return "redirect:/user/job_postings";
	}
	
	@GetMapping("/corp/main")
	public String corpMainPage(HttpServletRequest request, @AuthenticationPrincipal CustomUser user, Model model) {
		// @AuthenticationPrincipal : Spring Security가 SecurityContextHolder에서 현재 인증(Authentication) 정보를 꺼내서 주입해주는 어노테이션, 현재 없음 
		
		CustomUser loginUser = null;
		String header = request.getHeader("Authorization");
		if(header != null && header.startsWith("Bearer ")) {
			String accessToken = header.split(" ")[1];
			
		}
		
		//테스트
		
		return "corp/main_page";
	}
}
