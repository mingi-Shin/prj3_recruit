package kr.co.sist.corp.service;

import java.util.List;
import java.util.Map;

import kr.co.sist.corp.dto.InterviewOfferDTO;
import kr.co.sist.corp.dto.RecentlyViewedDTO;
import kr.co.sist.corp.dto.ResumeDetailDTO;
import kr.co.sist.corp.dto.TalentFilterDTO;
import kr.co.sist.corp.dto.TalentPoolDTO;

public interface TalentPoolService {
	//페이지 부분
	//전체 인재 리스트
//	List<TalentPoolDTO> getPaginatedTalents(String sortBy, String order, int offset, int size, Long corpNo);
//	//전체 인재 리스트 - 총 건수
//	int selectAllTalentTotalCount();
	  
	//스크랩 인재 리스트
  List<TalentPoolDTO> getScrappedTalents(Long corpNo, int offset, int size, String sortBy, String order);
  //스크랩 인재 리스트 - 총 건수
  int getScrappedTalentsTotalCount(Long corpNo);
  
  // 최근 열람한 인재 리스트
  // 이력서 상세 정보 조회
  List<Integer> getRecentlyViewedResumes(Long corpNo, int startRow, int endRow);
  List<TalentPoolDTO> getResumeDetailsBySeqs(List<Integer> resumeSeqs, String sortBy, String order);

  // 최근 열람한 인재 리스트- 총 건수
  int getRecentlyViewedTotalCount(Long corpNo);
  
  
  //기능 부분
  //필터
  List<TalentPoolDTO> getPaginatedTalents(TalentFilterDTO filterDTO);
  int selectAllTalentTotalCount(TalentFilterDTO filterDTO);

  
	//스크랩 기능
	String scrapResume(Long resumeSeq, Long corpNo);
  //면접 제안
  void sendInterviewOffer(InterviewOfferDTO ioDTO);
  //면접 제안 - 기업 정보 
  InterviewOfferDTO getCorpInfoByCorpNo(Long corpNo);
  //이력서 상세 정보
  TalentPoolDTO selectResumeDetail(int resumeSeq);
  //이력서 상세 보기(열람) + 메세지
	void viewResume(Long resumeSeq, Long corpNo);
} 
