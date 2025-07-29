package kr.co.sist.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.sist.user.dto.TechStackDTO;

@Mapper
public interface JobStackMapper {

	
	public List<TechStackDTO> selectJobPostingsByTechStack(String stackName);
}
