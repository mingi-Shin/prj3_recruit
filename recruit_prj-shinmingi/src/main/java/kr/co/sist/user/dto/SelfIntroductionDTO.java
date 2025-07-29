package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userSelfIntroductionDTO")
public class SelfIntroductionDTO {

	private int selfIntroductionSeq;
	private int resumeSeq;
	private String title;
	private String content;
}
