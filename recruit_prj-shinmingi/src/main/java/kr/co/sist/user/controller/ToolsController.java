package kr.co.sist.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ToolsController {

	@GetMapping("/user/tools/resume_coach")
	public String resumeCoach(Model model) {
		model.addAttribute("menuTitle", "AI 자소서 코칭");
		
		return "user/tools/resume_coach";
	}
	
	@GetMapping("/user/tools/salary_calculator")
	public String salaryCalculator(Model model) {
		model.addAttribute("menuTitle", "연봉계산기");
		return "user/tools/salary_calculator";
	}
	
	@GetMapping("/user/tools/unemp_calculator")
	public String umempCalculator(Model model) {
		model.addAttribute("menuTitle", "실업급여 계산기");
		return "user/tools/unemp_calculator";
	}
	
	@GetMapping("/user/tools/image_adjustment")
	public String imageAdjustment(Model model) {
		model.addAttribute("menuTitle", "사진 크기 조정");
		return "user/tools/image_adjustment";
	}
	
	@GetMapping("/user/tools/severance_calculator")
	public String severanceCalculator(Model model) {
		model.addAttribute("menuTitle", "퇴직금 계산기");
		return "user/tools/severance_calculator";
	}
}
