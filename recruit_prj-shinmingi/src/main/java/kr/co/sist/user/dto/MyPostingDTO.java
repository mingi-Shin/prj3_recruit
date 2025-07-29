package kr.co.sist.user.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userMyPostingDTO")
public class MyPostingDTO {

	private int jobPostingSeq;
	private String postingTitle;
	private String expLevel;
	private String region;
	private String district;
	private String corpNm;
	private String corpImg;
	private String positionName;
	private String stackName;
	private String postingEndDt;

	private int dday;
	private String ddayDisplay;

	//자동으로 디데이 계산
	public void setPostingEndDt(String postingEndDt) {
		this.postingEndDt = postingEndDt;
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate endDate = LocalDate.parse(postingEndDt, formatter);
		LocalDate today = LocalDate.now();

		long days = ChronoUnit.DAYS.between(today, endDate);
		this.dday = (int) days;
		
		if(this.dday < 0) {
			this.ddayDisplay = "마감";
		} else if(this.dday == 0) {
			this.ddayDisplay = "D-day";
		} else {
			this.ddayDisplay = "D-" + (int)days;
		}

	}

}
