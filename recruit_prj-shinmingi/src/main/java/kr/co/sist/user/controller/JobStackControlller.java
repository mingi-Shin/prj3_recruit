package kr.co.sist.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.sist.user.dto.TechStackDTO;
import kr.co.sist.user.service.JobStackService;

@Controller
public class JobStackControlller {
	
	private final JobStackService jss;
	
	@Autowired
	public JobStackControlller(JobStackService jss) {
		this.jss=jss;  
	}
	
	@GetMapping("/user/job_posting/job_postings_by_stack")
	public String getJobPostingsByTechStack(@RequestParam("stackName") String stackName, Model model) {
		
		List<TechStackDTO> techStackList=jss.getJobPostingsByTechStack(stackName);
		
		model.addAttribute("techStackList", techStackList);
		
		return "/user/job_posting/job_posting";
	}

}
