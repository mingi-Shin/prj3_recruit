package kr.co.sist.mail;

import org.springframework.stereotype.Service;

public interface MailSendService {

	/**
   * 인증 이메일 전송 비즈니스 로직
   */
  void sendVerificationEmail(MailHtmlSendDTO mailHtmlSendDTO, String clientIp);
  
  /**
   * HTML 이메일 전송
   */
  void sendHtmlEmail(MailHtmlSendDTO mailHtmlSendDTO); // MimeMessageHelper를 활용하여 HTML 기반 메일을 전송 
  
  /**
   * 가입 승인 전에 인증 여부 체크
   */
  boolean chkVerifiBeforeJoin(String email);
  
  /**
   * 입력된 인증번호 확인 체크  
   * @return 
   */
  boolean chkVeirifiCode(String email, String code);
  
  
  
  
}
