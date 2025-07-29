package kr.co.sist.mail;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailVerificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailVerifiSeq;

    private String email;
    
    private String ip;

    private String verificationCode;
    
    private int attemptCount = 0;

    private LocalDateTime createdAt = LocalDateTime.now();

    //인증 유지시간은 5분으로 고정 
    private LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);

    private String isVerified = "N";
}