package kr.co.sist.admin.review;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminReviewRestController {
	
	private final AdminReviewService adminReviewService;
	public AdminReviewRestController(AdminReviewService adminReviewService) {
		
		this.adminReviewService = adminReviewService;
	}
	
	@DeleteMapping("/admin/deleteReview")
	public String deleteReview(@RequestBody Map<String, Object> map) {
    
		
    boolean deleted = adminReviewService.deleteReview(map);
    
    if (deleted) {
        return "삭제 성공";
    } else {
        return "삭제 실패: 리뷰 없음";
    }
}
}


