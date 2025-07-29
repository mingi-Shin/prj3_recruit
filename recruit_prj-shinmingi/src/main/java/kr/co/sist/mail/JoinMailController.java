package kr.co.sist.mail;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.sist.globalController.Exceptions.EmailSendException;
import kr.co.sist.globalController.Exceptions.TooManyRequestsException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class JoinMailController {

	private final MailSendService mailSendService;

  @PostMapping("/signup/send-verification-email")
  public ResponseEntity<String> sendEmail(@RequestBody MailHtmlSendDTO mailHtmlSendDTO, HttpServletRequest request) {
      try {
          String clientIp = getClientIpAddress(request);
          mailSendService.sendVerificationEmail(mailHtmlSendDTO, clientIp);
          return ResponseEntity.ok("메일이 전송되었습니다.");
          
      } catch (TooManyRequestsException e) {
          return ResponseEntity
              .status(HttpStatus.TOO_MANY_REQUESTS)
              .body(e.getMessage());
              
      } catch (EmailSendException e) {
          return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("메일 전송 실패: " + e.getMessage());
              
      } catch (Exception e) {
          e.printStackTrace();
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("메일 전송 실패 : " + e.getMessage());
      }
  }
  /**
   * 클라이언트의 실제 IP 주소를 가져오는 메소드
   * 프록시나 로드밸런서를 거치는 경우를 고려
   */
  private String getClientIpAddress(HttpServletRequest request) {
      String xForwardedFor = request.getHeader("X-Forwarded-For");
      if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
          return xForwardedFor.split(",")[0].trim();
      }
      
      String xRealIp = request.getHeader("X-Real-IP");
      if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
          return xRealIp;
      }
      
      return request.getRemoteAddr();
  }
    
    
  /**
   *	인증번호 확인후 응답 
   */
  @PostMapping("/signup/confirm-verification-code")
  public ResponseEntity<String> confirmVerifiCode(@RequestBody MailHtmlSendDTO dto){
  	try {
      mailSendService.chkVeirifiCode(dto.getEmailAddr(), dto.getContent());
      System.out.println("인증 성공 로직 완료");
      return ResponseEntity.ok("인증 성공");
	  } catch (Exception e) {
	  	System.out.println("catch 블록 : " + e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
      // ResponseEntity.badRequest() => HTTP 상태 코드 400 (Bad Request)를 응답
      //서비스에서 넘어온 throw new IllegalStateException을 잡아서, e.getMessage()로 메시지를 꺼내서 ResponseEntity의 body에 그 문자열을 담고있다.
	  }
  }
   
    
    
}