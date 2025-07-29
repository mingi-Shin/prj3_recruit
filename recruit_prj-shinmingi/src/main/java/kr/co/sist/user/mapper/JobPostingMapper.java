package kr.co.sist.user.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.sist.corp.dto.JobPostingDTO;
import kr.co.sist.user.dto.JobPostDTO;

@Mapper
public interface JobPostingMapper {

  // 전체 공고 조회
  List<JobPostDTO> selectAllJobPostings();
  
  // 특정 공고 조회
  List<JobPostDTO> selectJobPostingsBySeq(Integer jobPostingSeq);
  
  // 특정 공고의 기술 스택 조회
  List<String> selectTechStacksByJobPostingSeq(Integer jobPostingSeq);
  
  // 조회수 업데이트
  void updateJobPostingViewCount(@Param("jobPostingSeq") Integer jobPostingSeq, @Param("viewCount") Integer viewCount);
  
  // 인기순 공고 가져오기 (조회수 기준)
  List<JobPostDTO> getPopularJobPostings();
  
  //특정 기업의 채용공고 목록 조회
  List<JobPostDTO> selectJobPostingsByCorpNo(long corpNo);
  
  //특정 기업의 활성화된 채용공고 조회 (현재 공고 제외)
  List<JobPostDTO> selectCompanyActiveJobs(Map<String, Object> params);
  
  
  
  
	/**
	 * 검색 조건, 페이징, 정렬 조건으로 조회
	 * @param dto 검색, 페이징, 정렬 
	 * @return 
	 */
	List<JobPostingDTO> selectSearchPosting(Map<String,Object> map);
	
	/**
	 * 검색 조건에 맞는 개수
	 * @param dto 검색조건
	 * @return 총 개수
	 */
	int countSearch(Map<String, Object> map);
	
	/**
	 * 체크 삭제
	 * @param map
	 */
	int deletePosting(Map<String,Object> map);
	

}