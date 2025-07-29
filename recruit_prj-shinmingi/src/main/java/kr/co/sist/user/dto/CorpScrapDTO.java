package kr.co.sist.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CorpScrapDTO {
	
	private long favoriteCompanySeq;  // favorite_company_seq
  private long corpNo;              // corp_no
  private String email;             // email
  private String scrapDate;

}
