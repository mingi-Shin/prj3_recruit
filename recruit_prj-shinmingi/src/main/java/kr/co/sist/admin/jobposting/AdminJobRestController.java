package kr.co.sist.admin.jobposting;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminJobRestController {

	private final AdminJobPostingService adminJobPostingService;
	
	public AdminJobRestController(AdminJobPostingService adminJobPostingService) {
		this.adminJobPostingService = adminJobPostingService;
	}
	
	@DeleteMapping("/admin/deleteJobPosting")
	public String deleteReview(@RequestBody Map<String, Object> map) {
		
		boolean deleted = adminJobPostingService.deletePosting(map);
		System.out.println("map : "+ map.get("job_posting_seq"));
		System.out.println("service : " + adminJobPostingService.deletePosting(map));
		if(deleted) {
			return "삭제 성공";
		} else {
			return "삭제 실패 : 공고 없음";
		}
	}
}
