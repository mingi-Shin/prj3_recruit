package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userMyReviewDTO")
public class MyReviewDTO {

	private int reviewSeq;
	private long corpNo;
	private String eamil;
	private int rating;
	private String summary;
	private String pros;
	private String cons;
	private String createdAt;
	private String corpNm;
	
}
