package kr.co.sist.admin.resister;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import kr.co.sist.admin.AdminDTO;
import kr.co.sist.admin.AdminEntity;
import kr.co.sist.admin.AdminRepository;
import kr.co.sist.admin.AdminService;

@Service
public class AdminRegisterService {

		private AdminRepository ar;
    private AdminService as;  // 기존 AdminService 이름 그대로
    private BCryptPasswordEncoder passwordEncoder;
    
    public AdminRegisterService(AdminRepository ar, AdminService as, BCryptPasswordEncoder passwordEncoder) {
      this.ar = ar;
      this.as = as;
      this.passwordEncoder = passwordEncoder;
  }
    
    
    
    /**
     * 회원가입 처리
     */
    public AdminEntity signUp(AdminDTO admin) {
        if (admin.getAdminId() == null || admin.getAdminId().isEmpty()) {
            throw new IllegalArgumentException("회원가입 실패");
        }

        if (ar.existsById(admin.getAdminId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        AdminEntity ae = AdminEntity.from(admin, passwordEncoder);
        // 비밀번호 암호화

        return ar.save(ae);
    }

    
    /**
     * 이메일 전송
     */
    public Map<String, Object> sendEmail(String email) {
        return as.sendEmail(email);
    }
}
