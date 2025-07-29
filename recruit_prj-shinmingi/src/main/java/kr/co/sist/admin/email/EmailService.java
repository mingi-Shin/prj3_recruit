package kr.co.sist.admin.email;

import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;  // 의존성 주입을 통해 필요한 객체를 가져옴
    private static final String senderEmail= "yangprj0705@gmail.com";
    private static String rNum;  // 랜덤 인증 코드

    // 랜덤 인증 코드 생성
    public static void createNumber() {
    	  SecureRandom secureRandom = new SecureRandom();
          StringBuilder sb = new StringBuilder();

          for (int i = 0; i < 6; i++) { // 6자리 숫자
              sb.append(secureRandom.nextInt(10)); // 0 ~ 9
          }
    
          rNum = sb.toString();
    }

    // 메일 양식 작성
    public MimeMessage createMail(String mail){
        createNumber();  // 인증 코드 생성
        MimeMessage message = javaMailSender.createMimeMessage();//javaMailSender를 통해 새 이메일 메시지(MimeMessage) 객체를 생성해서 message 변수에 담았다. ex) 이메일 만들기 위한 편지지를 message 변수에 담음

        try {
            message.setFrom(senderEmail);   // 보내는 이메일
            message.setRecipients(MimeMessage.RecipientType.TO, mail); // 보낼 이메일 설정
            message.setSubject("[민기인력] 회원가입을 위한 이메일 인증");  // 제목 설정
            String body = "";
            body += "<h1>" + "안녕하세요." + "</h1>";
            body += "<h1>" + "민기인력 입니다." + "</h1>";
            body += "<h3>" + "회원가입을 위한 요청하신 인증 번호입니다." + "</h3><br>";
            body += "<h2>" + "아래 코드를 회원가입 창으로 돌아가 입력해주세요." + "</h2>";

            body += "<div align='center' style='border:1px solid black; font-family:verdana;'>";
            body += "<h2>" + "회원가입 인증 코드입니다." + "</h2>";
            body += "<h1 style='color:blue'>" + rNum + "</h1>";
            body += "</div><br>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    // 실제 메일 전송
    public String sendEmail(String userId) {
        // 메일 전송에 필요한 정보 설정
        MimeMessage message = createMail(userId);
        // 실제 메일 전송
        javaMailSender.send(message);

        // 인증 코드 반환
        return rNum;
    }
    
    // 제재메일 양식 작성
       public MimeMessage sanctionMail(String mail,String name,String content){
           MimeMessage message = javaMailSender.createMimeMessage();//javaMailSender를 통해 새 이메일 메시지(MimeMessage) 객체를 생성해서 message 변수에 담았다. ex) 이메일 만들기 위한 편지지를 message 변수에 담음
           try {
               message.setFrom(senderEmail);   // 보내는 이메일
               message.setRecipients(MimeMessage.RecipientType.TO, mail); // 보낼 이메일 설정
               message.setSubject(name+"님은 제재되었습니다");  // 제목 설정
               String body = "";
               body += "<h1>" + "안녕하세요." + "</h1>";
               body += "<h1>" + "민기인력 입니다." + "</h1>";
               body += "<h3>" + name+"회원님은 아래와 같은 사유로 인해 제재되었습니다" + "</h3><br>";
               body += "<h3>" + content+ "</h3>";
               message.setText(body,"UTF-8", "html");
           } catch (Exception e) {
               e.printStackTrace();
           }

           return message;
       }
       // 실제 메일 전송
       public void sendSanctionEmail(String userId,String name,String content) {
           // 메일 전송에 필요한 정보 설정
           MimeMessage message = sanctionMail(userId,name,content);
           // 실제 메일 전송
           javaMailSender.send(message);
       }
}