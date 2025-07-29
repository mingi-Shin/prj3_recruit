package kr.co.sist.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.sist.user.dto.JobPostDTO;

@Mapper
public interface MainPageMapper {

	//검색
	List<JobPostDTO> searchJobPostings(@Param("keyword") String keyword);
	
	 List<String> getAutoCompleteSuggestions(@Param("term") String term);
}
