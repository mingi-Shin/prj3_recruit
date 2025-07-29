package kr.co.sist.corp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface JobPostingTechStackMapper {

	//jobPostingTechStack 테이블에 데이터 저장 / @Param사용 -> mapper.xml에서 parameterType 생략 가능
	public int insertjobPostingTechStack(@Param("jobPostingSeq") int jobPostingSeq, @Param("techStackSeq") int techStackSeq);
	
	//jobPostingTechStack 테이블에 데이터 수정 : 물리적삭제 / @Param사용 -> mapper.xml에서 parameterType 생략 가능
	public int deleteJobPostingTechStack(@Param("jobPostingSeq") int jobPostingSeq);
	
}
