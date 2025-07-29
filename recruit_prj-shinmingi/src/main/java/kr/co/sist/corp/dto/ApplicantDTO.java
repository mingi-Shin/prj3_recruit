package kr.co.sist.corp.dto;

import java.util.List;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("corpApplicantDTO")
public class ApplicantDTO {

	private long corpNo;
	private int jobApplicationSeq;
	private int resumeSeq, jobPostingSeq, applicationStatus, passStage;
	private String applicationDate, isRead, careerType, title, postingTitle, name, stageName, statusName;
	private List<ApplicationAttachmentDTO> attachmentList;
}
 