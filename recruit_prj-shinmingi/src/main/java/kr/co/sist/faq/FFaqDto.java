package kr.co.sist.faq;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FFaqDto {
  private Integer  faqSeq ; // ✅ faqSeq → 
  private String title;
  private String content;
  private String regsDate;
  private String modifyDate;
  // usertype 제거 또는 계산된 값으로 설정
  
  public static FFaqDto fromEntity(FFaqEntity entity) {
      FFaqDto dto = new FFaqDto();
      dto.setFaqSeq(entity.getFaqSeq());
      dto.setTitle(entity.getTitle());
      dto.setContent(entity.getContent());
      dto.setRegsDate(entity.getRegsDate());
      dto.setModifyDate(entity.getModifyDate());
      return dto;
  }
}