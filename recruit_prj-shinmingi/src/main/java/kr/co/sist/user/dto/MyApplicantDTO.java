package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userMyApplicantDTO")
public class MyApplicantDTO {

	private int jobApplicationSeq; //job_application
	private String applicationDate; //job_application
	private int applicationStatus; //job_application
	private int passStage; //job_application
	private int resumeSeq; //job_application
	private long corpNo; //jop_posting
	private String postingTitle; //job_posting
	private int jobPostingSeq; //job_posting
	private String postingEndDt; //job_posting
	private String corpNm; //company
	
	private String dDay;
	
}
