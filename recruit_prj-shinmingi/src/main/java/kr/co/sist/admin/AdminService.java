package kr.co.sist.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.co.sist.admin.email.EmailService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminService {
    
    @Autowired
    private AdminRepository ar;
    private final EmailService emailService;

    /**
     * 관리자 한명 찾아서 정보 조회(나중에 AdminService로 옮겨야함)
     * @return
     */
    public Map<String, Object> sendEmail(String email) {
        boolean flag = emailChk(email);
        Map<String, Object> response = new HashMap<>();
        
        if(flag) {  // 이메일이 이미 존재하는 경우
            response.put("exists", true);
            response.put("msg", "이미 사용 중인 이메일입니다.");
        } else {    // 이메일이 존재하지 않는 경우 (사용 가능)
            String code = emailService.sendEmail(email);
            response.put("exists", false);  // 존재하지 않음 = 사용 가능
            response.put("msg", "전송이 완료되었습니다.");
            response.put("code", code);
        }
        return response;
    }
    
    public boolean emailChk(String email) {
        Optional<AdminEntity> optional = ar.findById(email);
        return optional.isPresent();
    }
    
    public Page<AdminEntity> searchAdminsWithFilters(
        String searchType,
        String keyword,
        String dept,
        String job,
        String stat,
        int page,
        int size) {

    Pageable pageable = PageRequest.of(page, size);

    String deptFilter = "전체".equals(dept) ? null : dept;
    String jobFilter = "전체".equals(job) ? null : job;
    String statFilter = "전체".equals(stat) ? null : stat;
    String keywordFilter = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
    String searchTypeFilter = (searchType == null || searchType.trim().isEmpty()) ? "전체" : searchType;

    return ar.findAdminsWithCustomOrder(
            searchTypeFilter,
            keywordFilter,
            deptFilter,
            jobFilter,
            statFilter,
            pageable);
}
    /**
     * 관리자의 모든 정보를 조회
     */
    public Optional<AdminEntity> searchOneAdmin(String email) {
        return ar.findById(email);
    }
    
    
    @Transactional
    public void updateAdminStatus(List<String> adminIds, String status) {
        for (String adminId : adminIds) {
            Optional<AdminEntity> optAdmin = ar.findById(adminId);
            if (optAdmin.isPresent()) {
                AdminEntity admin = optAdmin.get();

                // 기존 값 유지 (중요)
                String prevApprovalDate = admin.getApprovalDate();
                String prevRequestDate = admin.getApprovalRequestDate();

                admin.setStat(status);

                if ("승인됨".equals(status)) {
                    if (prevApprovalDate == null) {
                        admin.setApprovalDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    }
                } else if ("탈퇴".equals(status)) {
                    admin.setResignationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    // 승인 요청일과 승인일 유지
                    admin.setApprovalDate(prevApprovalDate);
                    admin.setApprovalRequestDate(prevRequestDate);
                }

                ar.save(admin);
            }
        }
    }

    
}
