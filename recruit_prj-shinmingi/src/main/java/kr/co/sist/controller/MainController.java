package kr.co.sist.controller;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.sist.jwt.AuthController;
import kr.co.sist.jwt.CustomUser;
import kr.co.sist.jwt.JWTUtil;
import kr.co.sist.login.UserRepository;
import kr.co.sist.user.dto.UserDTO;
import kr.co.sist.user.entity.UserEntity;
import kr.co.sist.util.CipherUtil;

@Controller
public class MainController {

    private final AuthController authController;
	
	private final CipherUtil cu;

	private final JWTUtil jwtUtil;
	
	private final UserRepository ur;
	
	public MainController(JWTUtil jwtUtil, UserRepository ur, CipherUtil cipherUtil, CipherUtil cu, AuthController authController) {
		this.jwtUtil = jwtUtil;
		this.ur = ur;
		this.cu = cu;
		this.authController = authController;
	}
	
	@GetMapping("/")
	public String mainPage(HttpServletRequest request) {
		
		return "redirect:/user/job_postings";
	}
	
	@GetMapping("/corp/main")
	//@ResponseBody
	public String corpMainPage(HttpServletRequest request, @AuthenticationPrincipal CustomUser user, Model model) {
		// @AuthenticationPrincipal : Spring Security가 SecurityContextHolder에서 현재 인증(Authentication) 정보를 꺼내서 주입해주는 어노테이션, 현재 없음 
		
		CustomUser loginUser = null;
		String Authorization = request.getHeader("Authorization");
		System.out.println("@AuthenticationPrincipal 테스트중 = " + user); // 나옴 
		System.out.println("Authorization 테스트중 = " + Authorization); // 나옴 
		
		return "/corp/main_page";
	}
	
	@GetMapping("/corp/main_page")
	public String corpMainPage2(HttpServletRequest request, @AuthenticationPrincipal CustomUser user, Model model) {
		// @AuthenticationPrincipal : Spring Security가 SecurityContextHolder에서 현재 인증(Authentication) 정보를 꺼내서 주입해주는 어노테이션, 현재 없음 
		
		System.out.println("/corp/main_page 가는 중----------------");
	
		String Authorization = request.getHeader("Authorization");
		System.out.println("@AuthenticationPrincipal 테스트중 = " + user); // 나옴 
		System.out.println("Authorization 테스트중 = " + Authorization); // 나옴
		
		System.out.println("/corp/main_page 가는 끝 ----------------");
		
		/**
		 * access를 param으로 받아서 SecurityContextHolder에 넣는건..?
		 * 	•	URL 파라미터로 accessToken을 전달하면 브라우저 히스토리, 서버 로그, 프록시 로그 등 어디서든 토큰이 평문으로 노출됩니다.
				•	그러면 JWT를 localStorage에 넣고 헤더로만 쓰는 의미가 사라지고, 보안상 거의 무용지물이 돼요.
		 */
		return "/corp/main_page";
	}
	
	
	/**
	 * si_corp_practice_code.html 연습 바로가기 
	 */
	@GetMapping("/corp/si_practice")
	public String goToSiPractice() {
		
		return "/corp/si_corp_practice_code";
	}
	
	
	
	
	
	
}
