package kr.co.sist.corp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.sist.corp.dto.JobPostingTechStackDTO;

@Mapper
public interface JobPostingTechStackMapper {

	//jobPostingTechStack 테이블에 데이터 저장 / @Param사용 -> mapper.xml에서 parameterType 생략 가능
	public int insertjobPostingTechStack(@Param("jobPostingSeq") int jobPostingSeq, @Param("techStackSeq") int techStackSeq);
	
	//jobPostingTechStack 테이블에 데이터 저장
	public int insertjobPostingTechStackBatch(List<JobPostingTechStackDTO> jobPostingTechList);
	
	
	//jobPostingTechStack 테이블에 데이터 수정 : 물리적삭제 / @Param사용 -> mapper.xml에서 parameterType 생략 가능
	public int deleteJobPostingTechStack(@Param("jobPostingSeq") int jobPostingSeq);
	
}
