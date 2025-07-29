package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userMyApplicantSearchDTO")
public class MyApplicantSearchDTO {

	private String email;
	private String type;
	private String period;
	private String includeCanceled = "true";
	
	private int offset;
	private int currentPage;
}
