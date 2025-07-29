package kr.co.sist.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("admin")
public class AdminController {
	
	private final AdminService as;
	
	public AdminController(AdminService as) {
		this.as = as;
	}
	
	@GetMapping("/admin_list")
	public String adminList(
			@RequestParam(defaultValue = "전체") String searchType,
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "전체") String dept,
			@RequestParam(defaultValue = "전체") String job,
			@RequestParam(defaultValue = "전체") String stat,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) {
		
		Page<AdminEntity> adminPage = as.searchAdminsWithFilters(
			searchType, keyword, dept, job, stat, page, size);
		
		model.addAttribute("adminList", adminPage.getContent());
		model.addAttribute("totalPages", adminPage.getTotalPages());
		model.addAttribute("currentPage", adminPage.getNumber());
		model.addAttribute("totalCount", adminPage.getTotalElements());
		model.addAttribute("searchType", searchType);
		model.addAttribute("keyword", keyword);
		model.addAttribute("dept", dept);
		model.addAttribute("job", job);
		model.addAttribute("stat", stat);

		return "admin/admin_list";
	}
	
	@GetMapping("/admin_mainpage")
	public String mainPage() {
		return "admin/admin_mainpage";
	}

//	@GetMapping("/detail/{adminId}")
//	public String adminDetail(@PathVariable String adminId, Model model) {
//	    // 관리자 정보 조회 (Optional 처리)
//	    Optional<AdminEntity> optAdmin = as.searchOneAdmin(adminId);
//
//	    if (optAdmin.isEmpty()) {
//	        // 없는 관리자일 경우 404 페이지 또는 에러 페이지 리턴
//	        return "error/404"; 
//	    }
//
//	    AdminEntity admin = optAdmin.get();
//	    // 로그인 로그 서비스 주입이 필요하므로 필드로 선언 후 생성자 주입 필요
//	    List<AdminLoginLogDTO> loginLogs = alls.getLogByAdminId(adminId);
//
//	    model.addAttribute("admin", admin);
//	    model.addAttribute("loginLogs", loginLogs);
//
//	    return "admin/admin_detail";  // 상세 페이지 뷰명
//	}

	
	// 상태 변경 처리 메서드 (POST)
	@PostMapping("/updateStatus")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateStatus(@RequestBody Map<String, Object> payload) {
	    List<String> adminIds = (List<String>) payload.get("adminIds");
	    String status = (String) payload.get("status");
	    
	    Map<String, Object> result = new HashMap<>();

	    try {
    			as.updateAdminStatus(adminIds, status);
	        
	        result.put("success", true);
	        result.put("message", "상태가 성공적으로 변경되었습니다.");
	        return ResponseEntity.ok(result);
	    } catch (Exception e) {
	        e.printStackTrace();
	        result.put("success", false);
	        result.put("message", "상태 변경 중 오류가 발생했습니다.");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
	    }
	}
	
}
