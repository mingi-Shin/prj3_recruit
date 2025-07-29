package kr.co.sist.admin.login;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import kr.co.sist.admin.AdminDTO;
import kr.co.sist.admin.AdminEntity;
import kr.co.sist.admin.AdminRepository;

@Service
public class AdminLoginService {
	
   private final BCryptPasswordEncoder passwordEncoder; 
	 private final AdminRepository ar;
	 
	 public AdminLoginService(BCryptPasswordEncoder passwordEncoder, AdminRepository ar) {
		 this.passwordEncoder = passwordEncoder;
		 this.ar = ar;
	 }
	 public AdminDTO authenticate(String adminId, String rawPassword) {

		  // Optional로 받은 후 값이 있는지 확인하거나 예외 처리
	    AdminEntity admin = ar.findById(adminId)
	        .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));
	    

	    // 비밀번호 검증은 스프링 시큐리티에게 맡기고 여기선 안함
	    AdminDTO dto = AdminDTO.from(admin);

	    return dto;
 }
}
