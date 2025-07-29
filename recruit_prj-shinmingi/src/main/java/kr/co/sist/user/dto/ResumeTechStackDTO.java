package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userResumeTechStackDTO")
public class ResumeTechStackDTO {

	private int resumeTechStackSeq;
	private int techStackSeq, resumeSeq;
	private String stackName;
}
