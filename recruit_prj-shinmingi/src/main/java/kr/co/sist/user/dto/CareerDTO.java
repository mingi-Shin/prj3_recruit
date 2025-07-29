package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Alias("userCareerDTO")
@Getter
@Setter
@ToString
public class CareerDTO {

	private int careerSeq;
	private int resumeSeq, indexNum;
	private String companyName, position, careerDescription, startDate, endDate;
}
