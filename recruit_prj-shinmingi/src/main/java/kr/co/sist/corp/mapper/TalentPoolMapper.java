package kr.co.sist.corp.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.sist.corp.dto.InterviewOfferDTO;
import kr.co.sist.corp.dto.MessageDTO;
import kr.co.sist.corp.dto.RecentlyViewedDTO;
import kr.co.sist.corp.dto.ResumeScrapDTO;
import kr.co.sist.corp.dto.TalentFilterDTO;
import kr.co.sist.corp.dto.TalentPoolDTO;

@Mapper
public interface TalentPoolMapper {
	
	int checkScrapExists(@Param("resumeSeq") Long resumeSeq, @Param("corpNo") Long corpNo);

	int insertScrap(ResumeScrapDTO sDTO);

	ResumeScrapDTO selectScrap(@Param("resumeSeq") Long resumeSeq, @Param("corpNo") Long corpNo);
	
	int updateScrap(ResumeScrapDTO sDTO);
	
	int updateScrapN(ResumeScrapDTO sDTO);
	
	int deleteScrap(@Param("resumeSeq") Long resumeSeq, @Param("corpNo") Long corpNo);

	List<TalentPoolDTO> selectScrappedTalents(@Param("corpNo") Long corpNo);

	int isResumeScrapped(@Param("resumeSeq") Long resumeSeq, @Param("corpNo") Long corpNo);

	//전체 인재
  public List<TalentPoolDTO> selectAllTalents(TalentFilterDTO filterDTO); // 파라미터 타입 변경
  //전체 인재 총 건수
  int selectAllTalentTotalCount(TalentFilterDTO filterDTO);

    
  // 스크랩한 인재 목록 조회 (페이징)
  List<TalentPoolDTO> selectPaginatedScrappedTalents(	@Param("corpNo") Long corpNo,
																								      @Param("offset") int offset,
																								      @Param("size") int size,
																								      @Param("sortBy") String sortBy,  
																								      @Param("order") String order );    
  // 스크랩한 인재 총 건 수
  int getScrappedTalentsTotalCount(@Param("corpNo") Long corpNo);
  
  // 최근 열람한 이력서 수 조회
  int getRecentlyViewedTotalCount(Long corpNo);
  //최근 열람한 이력서 목록 조회
  List<Integer> getRecentlyViewedResumes(Map<String, Object> params);
  // 이력서 상세 정보 조회
  public List<TalentPoolDTO> selectResumeMemberInfo(
      @Param("resumeSeqList") List<Integer> resumeSeqList,
      @Param("sortBy") String sortBy,
      @Param("order") String order
  );

  //============기능 부분===============
  //툴바
  List<TalentPoolDTO> getSortedTalentPool(@Param("sortBy") String sortBy,
																		      @Param("order") String order,
																		      @Param("offset") int offset,
																		      @Param("size") int size,
																		      @Param("corpNo") Long corpNo);
  //필터
  List<TalentPoolDTO> selectFilteredTalents(Map<String, Object> paramMap);
  int selectFilteredTalentCount(Map<String, Object> paramMap);

  //면접 제안 메세지
  void insertInterviewProposal(InterviewOfferDTO ioDTO);
  //이력서 상세정보
  TalentPoolDTO selectResumeDetail(int resumeNo);
  
  //이력서 열람 확인
  int checkResumeViewExist(@Param("resumeSeq") Long resumeSeq, @Param("corpNo") Long corpNo);
  //이력서 열람 기록
  void insertResumeViewLog(@Param("resumeSeq") Long resumeSeq, @Param("corpNo") Long corpNo);
  //이력서 정보
  TalentPoolDTO selectResumeInfo(@Param("resumeSeq") Long resumeSeq);
  //기업명
  InterviewOfferDTO getCorpInfoByCorpNo(Long corpNo);
  //이력서 열람 메세지 보내기
  void insertMessage(MessageDTO mail);
  
}
