package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userProjectStackDTO")
public class ProjectTechStackDTO {

	private int projectTechStackSeq;
	private int projectSeq, techStackSeq;
	private String stackName;
}
