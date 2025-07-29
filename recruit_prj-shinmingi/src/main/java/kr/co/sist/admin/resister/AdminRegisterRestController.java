package kr.co.sist.admin.resister;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.sist.admin.AdminDTO;
import kr.co.sist.admin.AdminEntity;
import kr.co.sist.admin.AdminService;
import kr.co.sist.admin.email.EmailService;
import kr.co.sist.corp.controller.CorpProductController;
import kr.co.sist.util.CipherUtil;
import lombok.RequiredArgsConstructor;

@RestController
public class AdminRegisterRestController {

    @Autowired
    private AdminRegisterService adminRegisterService;
    
    @PostMapping("/sendEmail")
    public Map<String, Object> mailConfirm(@RequestBody Map<String,String> request) {
        String email = request.get("email");
        return adminRegisterService.sendEmail(email);
    }
    
    @PostMapping("/admin_register_process")
    public Map<String, Object> doSignUp(@RequestBody AdminDTO admin) {
        Map<String, Object> response = new HashMap<>();
        try {
            adminRegisterService.signUp(admin);
            response.put("success", true);
            response.put("message", "회원가입이 완료되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "회원가입에 실패했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }
}
