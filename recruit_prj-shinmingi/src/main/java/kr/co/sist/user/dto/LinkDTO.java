package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userLinkDTO")
public class LinkDTO {

	private int linkSeq;
	private int resumeSeq;
	private String githubUrl, notionUrl, blogUrl;
}
