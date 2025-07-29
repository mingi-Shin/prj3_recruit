package kr.co.sist.login;

import kr.co.sist.mail.JoinMailController;
import kr.co.sist.mail.MailHtmlSendDTO;
import kr.co.sist.user.dto.UserDTO;
import kr.co.sist.user.entity.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/account")
public class AccountRecoveryController {

    private final LoginJoinService ljs;
    private final JoinMailController joinMailController;

    public AccountRecoveryController(LoginJoinService ljs, JoinMailController joinMailController) {
        this.ljs = ljs;
        this.joinMailController = joinMailController;
    }
    
    @GetMapping("/foundID")
    public String foundIDPage() {
        return "login/foundID";
    }

    /**
     * 아이디(이메일) 찾기
     */
    @PostMapping("/foundID")
    @ResponseBody
    public ResponseEntity<?> findEmailByNameAndHp(@RequestParam("name") String name, 
                                                 @RequestParam("phone") String phone,
                                                 HttpServletRequest request) {
        AccountRecoveryDTO user = ljs.findByNameAndPhone(name, phone);
        if (user != null) {
            try {
                String clientIp = request.getHeader("X-Forwarded-For");
                if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                    clientIp = request.getRemoteAddr();
                }

                // MailHtmlSendDTO 생성
                MailHtmlSendDTO mailDto = new MailHtmlSendDTO();
                mailDto.setEmailAddr(user.getEmail());
                mailDto.setSubject("아이디 찾기 결과");
                mailDto.setContent("회원님의 이메일(ID)은 다음과 같습니다:\n\n" + user.getEmail());
                mailDto.setTarget("아이디 찾기");

                // JoinMailController의 sendEmail 메서드 호출 (메일 전송)
                joinMailController.sendEmail(mailDto, request);

                return ResponseEntity.ok("이메일이 전송되었습니다.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송 실패");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 회원이 없습니다.");
        }
    }


    /**
     * 비밀번호 재설정 전 이메일 존재 여부 확인
     */
    @PostMapping("/checkEmail")
    @ResponseBody
    public ResponseEntity<?> checkEmailExists(@RequestParam("email") String email) {
        UserDTO user = ljs.authenticate(email, null); // 비밀번호는 null 전달
        if (user != null) {
            return ResponseEntity.ok("이메일 확인 완료");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 이메일입니다.");
        }
    }

    @PostMapping("/resetPassword")
    @ResponseBody
    public ResponseEntity<?> resetPassword(
        @RequestParam("email") String email,
        @RequestParam("newPassword") String newPassword,
        HttpServletRequest request
    ) {
        String verifiedEmail = (String) request.getSession().getAttribute("verifiedEmail");
        if (verifiedEmail == null || !verifiedEmail.equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이메일 인증이 필요합니다.");
        }

        UserDTO uDTO = new UserDTO();
        uDTO.setEmail(email);
        uDTO.setPassword(newPassword);

        try {
            UserEntity updated = ljs.updatePassword(uDTO);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 변경 실패");
        }
    }
}
