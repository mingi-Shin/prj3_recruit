package kr.co.sist.user.entity;

import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="ASK")
public class InquiryEntity {
    
		@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
		
    @Column(name = "ASK_SEQ")
    private Long askSeq;
    
    @Column(name = "EMAIL", length = 100)
    private String email;
    
    @Column(name = "ADMIN_ID", length = 50)
    private String adminId;
    
    @Column(name = "TITLE", length = 200, nullable = false)
    private String title;
    
    @Column(name = "CONTENT", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "ANSWER", columnDefinition = "TEXT")
    private String answer;
    
    @Column(name = "REGS_DATE")
    private LocalDateTime regsDate;
    
    @Column(name = "CATEGORY", length = 50)
    private String category;
    
    @Column(name = "ANSWER_STAT", length = 1, nullable = false)
    private String answerStat = "N"; // N: 답변대기, Y: 답변완료
    
    @Column(name = "USER_TYPE", length = 20)
    private String userType;
    
    @PrePersist
    protected void onCreate() {
        regsDate = LocalDateTime.now();
    }

    
}