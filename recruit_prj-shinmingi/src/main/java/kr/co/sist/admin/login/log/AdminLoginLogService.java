package kr.co.sist.admin.login.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import kr.co.sist.admin.AdminRepository;

@Service
public class AdminLoginLogService {
	
	private final	AdminLoginLogMapper adminLoginLogMapper;
	private AdminRepository adminRepository;
	
	public AdminLoginLogService(AdminLoginLogMapper adminLoginLogMapper, AdminRepository AdminRepository) {
		this.adminLoginLogMapper = adminLoginLogMapper;
		this.adminRepository = AdminRepository;
	}
	
	public void logLogin(String adminEmail, boolean success,
	      String ip, String userAgent, String failReason) {

				boolean exists = adminRepository.existsById(adminEmail);
				if (!exists) {
				    return;
				}
		
				// 1. 현재 시간 문자열 생성 (yyyy-MM-dd HH:mm:ss)
				String now = LocalDateTime.now()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				
				
				
				// 2. 로그인 로그 테이블에 기록할 파라미터 생성
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("adminId", adminEmail);
				param.put("loginTime", now);
				param.put("loginIp", ip);
				param.put("userAgent", userAgent);
				param.put("loginResult", success ? "Y" : "N");
				param.put("loginFail", success ? "SUCCESS" : failReason);
		    
				
				
				
				// 3. 로그 기록
				adminLoginLogMapper.insertLoginLog(param);
				
				if (success) {
				// 4. 성공 시 누적 실패 횟수 초기화
					adminLoginLogMapper.resetLoginFailCount(adminEmail);
				} else {
				// 5. 실패 시 누적 실패 횟수 증가
					adminLoginLogMapper.increaseLoginFailCount(adminEmail);
				
				// 6. 누적 실패 횟수 조회
				int failCount = adminLoginLogMapper.selectLoginFailCount(adminEmail);
				
				// 7. 누적 실패 5회 이상 시 계정 잠금 처리
				if (failCount >= 5) {
					adminLoginLogMapper.lockAccount(adminEmail);
			}
		}
	}
	
		public List<AdminLoginLogDTO> getLoginLogs(String adminId){
				
			return adminLoginLogMapper.selectTop5LoginLogs(adminId);
		}
}
