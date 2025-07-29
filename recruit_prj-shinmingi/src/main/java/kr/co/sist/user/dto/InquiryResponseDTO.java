package kr.co.sist.user.dto;

import java.time.LocalDateTime;

import kr.co.sist.user.entity.InquiryEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InquiryResponseDTO {
    private Long askSeq;
    private String email;
    private String adminId;
    private String title;
    private String content;
    private String answer;
    private LocalDateTime regsDate;
    private String category;
    private String answerStat;
    private String userType;
    public InquiryResponseDTO() {
    	
    }
    
    public static InquiryResponseDTO fromEntity(InquiryEntity entity) {
      InquiryResponseDTO dto = new InquiryResponseDTO();
      dto.setAskSeq(entity.getAskSeq());
      dto.setEmail(entity.getEmail());
      dto.setAdminId(entity.getAdminId());
      dto.setTitle(entity.getTitle());
      dto.setContent(entity.getContent());
      dto.setAnswer(entity.getAnswer());
      dto.setRegsDate(entity.getRegsDate());
      dto.setCategory(entity.getCategory());
      dto.setAnswerStat(entity.getAnswerStat());
      dto.setUserType(entity.getUserType());
      return dto;
  }
    
    
}