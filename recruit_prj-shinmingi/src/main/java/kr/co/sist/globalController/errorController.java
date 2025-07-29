package kr.co.sist.globalController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class errorController {

	@GetMapping("/accessDenied")
	public String errorPage() {
		return "error/accessDenied";
	}
	
	@GetMapping("/error/redirect")
	public String accessDenied(HttpServletRequest request, Model model ) {
		
		String msg = request.getAttribute("msg").toString();
		model.addAttribute("msg", msg);
		
		return "login/login_form";
	}
}
