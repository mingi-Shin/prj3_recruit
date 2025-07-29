package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userEducationHistoryDTO")
public class EducationHistoryDTO {

	private int educationHistorySeq;

	private int resumeSeq, indexNum;
	private String schoolName, department, educationType, admissionDate, graduateDate;
	private double grade, standardGrade;
}
