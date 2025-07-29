package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userResumeDTO")
public class ResumeDTO {

	private int resumeSeq;
	private String email, title, image, introduction, createdAt, updatedAt, careerType, isPublic;
}
