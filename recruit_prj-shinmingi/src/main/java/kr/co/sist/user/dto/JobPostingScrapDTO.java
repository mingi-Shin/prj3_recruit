package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userJobPostingScrapDTO")
public class JobPostingScrapDTO {

	private int scrapSeq, jobPostingSeq;
	private String email, scrapDate;
}
