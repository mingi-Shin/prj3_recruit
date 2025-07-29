package kr.co.sist.corp.dto;

import lombok.Data;

@Data
public class InterviewOfferDTO {

  private Long corpNo;
  private Long resumeSeq;
	private String email;
	private String messageTitle;
	private String messageContent;
	private String createdAt;  
	private String isRead;     
	private String readedAt;   
	private String isOffered;
	
	private String corpEmail;
	private String corpName;
	private String corpUrl;
	
}