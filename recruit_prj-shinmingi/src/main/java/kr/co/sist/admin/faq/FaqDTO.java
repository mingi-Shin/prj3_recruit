package kr.co.sist.admin.faq;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FaqDTO {
  private int faqSeq;
  private String title;
  private String content;
  private String regsDate;
  private String modifyDate;
  private String adminId;

  public FaqDTO(FaqEntity entity) {
      this.faqSeq = entity.getFaqSeq();
      this.title = entity.getTitle();
      this.content = entity.getContent();
      this.regsDate = entity.getRegs_date();
      this.modifyDate = entity.getModify_date();
      //this.adminId = entity.getAdmin() != null ? entity.getAdmin().getAdminId() : null;
      this.adminId = entity.getAdminId() != null ? entity.getAdminId().getAdminId() : null;
  }
}
