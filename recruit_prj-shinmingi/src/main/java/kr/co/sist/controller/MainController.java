package kr.co.sist.controller;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

		if(user == null) {
			//System.out.println("디버깅 / 회원정보 상태 : " + "비회원, 로그인 필요 ");
			
		} else {
			//System.out.println("디버깅 / 회원정보 상태 : " + user);
			UserEntity ueEntity = ur.getById(user.getEmail());
			
			if(ueEntity.getPhone() != null) {
				String enUserPhone = ueEntity.getPhone();
				String deUserPhone = cu.decryptText(enUserPhone);
				
				
				model.addAttribute("encryptPhone", enUserPhone);
				model.addAttribute("decryptPhone", deUserPhone);
			}
		}
		
		//테스트
		
		return "corp/main_page";
	}
}
