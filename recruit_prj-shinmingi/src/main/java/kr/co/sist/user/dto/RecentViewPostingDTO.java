package kr.co.sist.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RecentViewPostingDTO {
	
	private Integer recentViewPostingSeq;
	private String email;
	private Integer jobPostingSeq;
	private String openedAt;


}
