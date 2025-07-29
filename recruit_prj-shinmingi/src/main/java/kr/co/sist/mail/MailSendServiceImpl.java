package kr.co.sist.mail;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.co.sist.globalController.Exceptions.EmailSendException;
import kr.co.sist.globalController.Exceptions.TooManyRequestsException;

@Service
public class MailSendServiceImpl implements MailSendService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MailRepository mailRepository;

    public MailSendServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine, MailRepository mailRepository) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.mailRepository = mailRepository;
    }

    /**
     * 인증 이메일 전송 로직임 
     */
    public void sendVerificationEmail(MailHtmlSendDTO mailHtmlSendDTO, String clientIp) {
        // 인증 이메일로 기존 객체 확인
        List<MailVerificationEntity> entities = mailRepository.findAllByEmail(mailHtmlSendDTO.getEmailAddr());

        // 인증기록이 존재할 때, 제일 최근 것
        MailVerificationEntity lasted = findLatestValidEntity(entities);

        // 5회 초과 시도 후 30분 제한 체크
        validateAttemptLimit(entities);

        // 인증 상태별 분기 처리
        processVerificationRequest(mailHtmlSendDTO, clientIp, entities, lasted);
    }

    /**
     *  최근 인증 객체  찾기
     */
    private MailVerificationEntity findLatestValidEntity(List<MailVerificationEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return null;
        }
        
        return entities.stream()
            .filter(e -> e.getIsVerified().equals("N") && e.getExpiredAt().isAfter(LocalDateTime.now()))
            .max(Comparator.comparing(MailVerificationEntity::getCreatedAt))
            .orElse(null);
    }

    /**
     * 시도 횟수 제한 검증
     */
    private void validateAttemptLimit(List<MailVerificationEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }

        // 가장 최근 인증 시도 찾기
        MailVerificationEntity lastAttempt = entities.stream()
            .max(Comparator.comparing(MailVerificationEntity::getCreatedAt))
            .orElse(null);
        
        if (lastAttempt != null && lastAttempt.getAttemptCount() >= 5) {
            LocalDateTime restrictionEndTime = lastAttempt.getCreatedAt().plusMinutes(30);
            if (LocalDateTime.now().isBefore(restrictionEndTime)) {
                throw new TooManyRequestsException("인증 요청이 5회를 초과하였습니다. 30분 후 다시 시도해주세요.");
            }
        }
    }

    /**
     * 인증 요청 처리
     */
    private void processVerificationRequest(MailHtmlSendDTO mailHtmlSendDTO, String clientIp, 
                                          List<MailVerificationEntity> entities, MailVerificationEntity lasted) {
        
        // 인증이 처음일때 또는 30분 제한이 해제된 경우
        if (entities == null || entities.isEmpty() || lasted == null) {
            createAndSendVerification(mailHtmlSendDTO, clientIp, 1);
        // 인증만료 시간 전에, 재전송버튼을 누름으로 인해 이전의 인증코드를 강제 만료 처리하고 새로운 번호 재전송
        } else if (lasted.getIsVerified().equals("N") && lasted.getExpiredAt().isAfter(LocalDateTime.now())) {
            // 가장 최근 객체 강제 만료
            lasted.setExpiredAt(LocalDateTime.now());
            mailRepository.save(lasted);
            
            // 새로운 인증 생성 (시도 횟수 증가)
            createAndSendVerification(mailHtmlSendDTO, clientIp, lasted.getAttemptCount() + 1);
            
        // 30분 제한 해제 후 다시 시도하는 경우 (attemptCount 리셋)
        } else {
            createAndSendVerification(mailHtmlSendDTO, clientIp, 1); // 카운트 리셋
        }
    }

    /**
     * 인증 엔티티 생성 및 이메일 전송
     */
    private void createAndSendVerification(MailHtmlSendDTO mailHtmlSendDTO, String clientIp, int attemptCount) {
        // 인증번호 생성
        int verificationCode = createSecureNumber();
        mailHtmlSendDTO.setContent(String.valueOf(verificationCode));
        
        MailVerificationEntity mvEntity = MailVerificationEntity.builder()
            .email(mailHtmlSendDTO.getEmailAddr())
            .verificationCode(String.valueOf(verificationCode))
            .ip(clientIp)
            .attemptCount(attemptCount)
            .createdAt(LocalDateTime.now())
            .expiredAt(LocalDateTime.now().plusMinutes(5))
            .isVerified("N")
            .build();

        mailRepository.save(mvEntity);
        System.out.println("디버깅 인증 엔티티: " + mvEntity);
        
        sendHtmlEmail(mailHtmlSendDTO); // 인증메일 전송
    }

    /**
     * 가입 승인 전에 인증 여부 체크 -> 아, 이걸 쓰는게 나을까 
     */
    public boolean chkVerifiBeforeJoin(String email) {
        List<MailVerificationEntity> entities = mailRepository.findAllByEmail(email);
        
        if (entities == null || entities.isEmpty()) {
        		System.out.println("인증기록이없음 ");
            return false;
        }
        
        // 가장 최근의 인증 완료된 엔티티 찾기
        return entities.stream()
            .filter(e -> e.getIsVerified().equals("Y"))
            .max(Comparator.comparing(MailVerificationEntity::getCreatedAt))
            .map(entity -> entity.getExpiredAt().isAfter(LocalDateTime.now())) // 만료 시간 체크
            .orElse(false);
    }

    /**
     * HTML 이메일 전송 (Thymeleaf 템플릿 사용)
     */
    @Override
    public void sendHtmlEmail(MailHtmlSendDTO mailHtmlSendDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Context context = new Context();
            //mailHtmlSendDTO.setContent(String.valueOf(createSecureNumber())); // 난수를 Context에 담아줘.

            context.setVariable("subject", mailHtmlSendDTO.getSubject());
            context.setVariable("message", mailHtmlSendDTO.getContent());
            if(mailHtmlSendDTO.getTarget().equals("user")) {
                context.setVariable("userType", "일반 구직자");
            }
            if(mailHtmlSendDTO.getTarget().equals("corp")) {
                context.setVariable("userType", "기업 회원");
            }
            
            // static/images/logo.png 파일을 CID 이름으로 첨부 --> 이거 안되고있음 
            File logoFile = new ClassPathResource("static/images/logo.png").getFile();
            helper.addInline("logoImage", logoFile);
            context.setVariable("logoImage", logoFile);
            
            String htmlContent = templateEngine.process("login/email-template", context);
            helper.setTo(mailHtmlSendDTO.getEmailAddr());
            helper.setSubject(mailHtmlSendDTO.getSubject());
            helper.setText(htmlContent, true);
            mailSender.send(message);
            
        } catch(MessagingException e) {
            System.out.println("[-] Thymeleaf 템플릿 이메일 전송 중 오류 발생: " + e.getMessage());
            throw new EmailSendException("이메일 전송 실패: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new EmailSendException("이메일 전송 실패: " + e.getMessage());
        }
    }
    
    //이미지를 Base64로 인코딩하는 메서드
    private String getBase64EncodedImage(String imagePath) throws IOException {
        Resource resource = new ClassPathResource(imagePath); //classpath 내 리소스
        byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    //인증번호, 토큰, 비밀번호 생성 등 보안에 민감한 기능에는 SecureRandom이 강력 추천됩니다.
    //보안상 더 강력하다는데, 뭐가? : https://chatgpt.com/s/t_6863bf3e289c8191b1239d8a4f0ee1b2
    private int createSecureNumber() {
        int secureNumber = -1;
        
        SecureRandom secureRandom = new SecureRandom();

        // 6자리 인증번호
        secureNumber = secureRandom.nextInt(900000) + 100000; // 100000 ~ 999999
    
        // 랜덤 바이트 배열 (예: 토큰 생성)
        byte[] tokenBytes = new byte[16];
        secureRandom.nextBytes(tokenBytes);
        
        return secureNumber;
    }

    /**
     * 인증번호 확인하기 
     */
		@Override
		public boolean chkVeirifiCode(String email, String code) {
			
			System.out.println("디버깅 -> 입력이메일 : " +  email + "입력한 코드 : " + code );
			
			try {
				//일단 최근거 가져와
				List<MailVerificationEntity> entities = mailRepository.findAllByEmail(email);
				
				if (entities == null || entities.isEmpty()) {
          System.out.println("인증 객체가 없어 인증 코드 확인이 불가");
          throw new IllegalStateException("인증 실패: 유효한 인증 기록이 없습니다.");
				}
				
				// 유효한 가장 최신 객체 찾기
        MailVerificationEntity latest = findLatestValidEntity(entities);

        System.out.print("디버깅  lastest = ");
        System.out.println(latest);
        if (latest == null || latest.getIsVerified().equals("Y") ) {
            throw new IllegalStateException("인증 실패: 유효한 인증 기록이 존재하지 않거나, 이미 인증이 완료된 이메일입니다 ");
        }
        
        // 만료 시간 체크
        if (latest.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("인증 실패: 인증 시간이 만료되었습니다.");
        }

        // 인증 코드 비교
        if (!latest.getVerificationCode().equals(code)) {
            throw new IllegalStateException("인증 실패: 인증 코드가 일치하지 않습니다.");
        }
        
        //성공시에 
        latest.setIsVerified("Y"); // 인증 성공으로 상태 변경 (필요 시)
        mailRepository.save(latest); // 변경사항 저장
        return true;
				
				
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
				/**
				 * 컨트롤러에서 예외처리를 잡아다 뷰에 던지고 있으므로, 서비스에서는 try-catch를 없애는게 깔끔.
				 * 여기서 IllegalStateException을 이미 잡아버리고, 다시 던지지 않기 때문에, 컨트롤러는 예외를 절대 인식하지 못해.
				 * 그래서 컨트롤러는 이렇게 착각하지:
				 * "오~ 예외 없네? 인증 성공인가 보지?
				 * 	→ "✅ 인증 성공 로직 완료" 출력 → "인증 성공" 반환
				 * 
				 * 따라서 다시 throw e; 로 던져주거나 try-catch를 삭제하거나 ~
				 */
			}
		}


}