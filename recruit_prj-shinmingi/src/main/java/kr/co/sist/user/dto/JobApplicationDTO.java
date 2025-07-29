package kr.co.sist.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JobApplicationDTO {
	
	
			private Integer jobApplicationSeq;   // 지원 일련번호
	    private Integer resumeSeq;           // 이력서 일련번호
	    private Integer jobPostingSeq;       // 공고 일련번호
	    private Integer applicationStatus;    // 지원 상태 (예: 지원완료)
	    private String applicationDate;   // 지원 날짜
	    private String interviewDate;     // 면접 날짜
	    private String isRead;               // 읽음 여부 (예: Y / N)
	    private Integer passStage;

}
