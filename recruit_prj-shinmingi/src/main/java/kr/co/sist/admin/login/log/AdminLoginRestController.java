package kr.co.sist.admin.login.log;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminLoginRestController {
	
	private final AdminLoginLogService adminLoginLogService;
	
	public AdminLoginRestController(AdminLoginLogService adminLoginLogService) {
		this.adminLoginLogService = adminLoginLogService;
	}
	@GetMapping("/admin/loginLogs/{adminId}")
	public List<AdminLoginLogDTO> getLoginLogs(@PathVariable String adminId){
		return adminLoginLogService.getLoginLogs(adminId);
	}
}
