package kr.co.sist.admin.ask;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminInquiryRestController {

		private final AdminInquiryService adminInquiryService;
		
		public AdminInquiryRestController(AdminInquiryService adminInquiryService) {
			this.adminInquiryService = adminInquiryService;
		}
		
		@DeleteMapping("/admin/deleteInquiry")	
		public String deleteInquiry(@RequestBody Map<String, Object> map){
			
			boolean deleted = adminInquiryService.deleteInquiry(map);
			
			if(deleted) {
				return "삭제 성공";
			}else {
				return "삭제 실패 : 문의 없음";
			}
		}
	
    @PatchMapping("/admin/updateInquiry")
    public String updateInquiry(@RequestBody Map<String,Object> map) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminId = auth.getName(); // 보통 username (관리자 ID)
        
        // JavaScript에서 보낸 adminId 대신 실제 인증된 관리자 ID 사용
        map.put("adminId", adminId); // 키 이름을 mapper.xml과 일치시킴
        
        boolean updated = adminInquiryService.updateInquiry(map);
        if(updated) {
            return "답변 성공";
        }else {
            return "답변 실패";
        }
    }

    @GetMapping("/admin/api/inquiry/{askSeq}")
    @ResponseBody
    public AdminInquiryDTO getNoticeById(@PathVariable("askSeq") int askSeq) {
        return adminInquiryService.getInquiry(askSeq);
    }
	    
}
