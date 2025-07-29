package kr.co.sist.user.dto;

import java.util.List;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userProjectDTO")
public class ProjectDTO {

	private int projectSeq;
	private int resumeSeq;
	private String projectName, projectContent, startDate, endDate, releaseStatus, repositoryLink;
	private List<ProjectTechStackDTO> projectSkills;
}
