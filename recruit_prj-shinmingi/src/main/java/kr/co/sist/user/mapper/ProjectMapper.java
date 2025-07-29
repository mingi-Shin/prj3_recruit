package kr.co.sist.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.sist.user.dto.ProjectDTO;
import kr.co.sist.user.dto.ProjectTechStackDTO;

@Mapper
public interface ProjectMapper {
	
	public List<ProjectDTO> selectProjectByResume(int resumeSeq);
	public List<ProjectTechStackDTO> selectProjectStackByresume(int projectSeq);
}
