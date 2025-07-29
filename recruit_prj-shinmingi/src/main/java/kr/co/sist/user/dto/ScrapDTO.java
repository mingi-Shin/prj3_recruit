package kr.co.sist.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScrapDTO {
	
  private Integer scrapSeq;          // 스크랩 PK (생략 가능)
  private Integer jobPostingSeq;  // 공고 번호
  private String email;           // 사용자 이메일
  private String scrapDate;       // 스크랩 날짜 (yyyy-MM-dd HH:mm:ss 형식)


}
