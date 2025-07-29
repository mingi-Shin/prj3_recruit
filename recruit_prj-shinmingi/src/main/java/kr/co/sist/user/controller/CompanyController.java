package kr.co.sist.user.controller;

import java.text.NumberFormat;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.sist.user.dto.CompanyDTO;
import kr.co.sist.user.dto.JobPostDTO;
import kr.co.sist.user.service.CompanyService;
import kr.co.sist.user.service.JobPostingService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CompanyController {

		private final CompanyService cms;
		private final JobPostingService jps;
	
	  @GetMapping("/user/job_posting/company_info")
		public String getCompanyInfo(@RequestParam(required = true) long corpNo, Model model) {
			
	  	CompanyDTO companyDTO = cms.getCompanyInfoByCorpNo(corpNo);
	  	
	  	List<JobPostDTO> jobList=jps.getJobPostsByCorpNo(corpNo);
	  	
	  	model.addAttribute("jobList",jobList);
	  	model.addAttribute("companyDTO", companyDTO);
	  	
	    NumberFormat numberFormat = NumberFormat.getInstance();
      String corpAvgSal = numberFormat.format(companyDTO.getCorpAvgSal()) + "만원";
      String corpAnnualRevenue = numberFormat.format(companyDTO.getCorpAnnualRevenue()) + "억원";
      model.addAttribute("corpAvgSal", corpAvgSal);
      model.addAttribute("corpAnnualRevenue", corpAnnualRevenue);
	  	
	  	return "user/job_posting/company_info";
		}
	  

}
