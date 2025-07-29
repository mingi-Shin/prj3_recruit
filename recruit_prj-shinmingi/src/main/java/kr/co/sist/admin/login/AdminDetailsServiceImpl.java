package kr.co.sist.admin.login;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kr.co.sist.admin.AdminEntity;
import kr.co.sist.admin.AdminRepository;
import kr.co.sist.jwt.CustomAdmin;

@Service
public class AdminDetailsServiceImpl implements UserDetailsService {

    private final AdminRepository adminRepository;

    public AdminDetailsServiceImpl(AdminRepository adminRepository) {
    	this.adminRepository = adminRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String adminId) throws UsernameNotFoundException {
    	

        
        AdminEntity admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 관리자 ID입니다."));

        if ("계정잠김".equals(admin.getStat())) {
          throw new LockedException("계정이 잠긴 상태입니다.");
      }
        if ("탈퇴".equals(admin.getStat())) {
        	throw new DisabledException("탈퇴한 사용자입니다.");
        }
        if ("승인대기".equals(admin.getStat())) {
        	throw new DisabledException("미승인 계정입니다.");
        }
        
        CustomAdmin customAdmin = new CustomAdmin(admin);

        return customAdmin;
    }

}
