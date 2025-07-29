package kr.co.sist.admin.ask;

import lombok.Getter;

@Getter
public class AdminInquiryDTO {

	private Integer askSeq;
	private String email;
	private String adminId;
	private String title;
	private String content;
	private String answer;
	private String regsDate;
	private String category;
	private Character answerStat;
	private String userType;
	private String filePath;
	private String answerDate;	


}
