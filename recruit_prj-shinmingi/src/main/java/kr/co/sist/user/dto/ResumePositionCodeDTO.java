package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userResumePositionCodeDTO")
public class ResumePositionCodeDTO {

	private int resumePositionSeq;
	private int resumeSeq, positionSeq;
	private String positionName;
}
