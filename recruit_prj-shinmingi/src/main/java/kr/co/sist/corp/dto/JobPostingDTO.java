package kr.co.sist.corp.dto;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.sist.user.dto.TechStackDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobPostingDTO {

  private int jobPostingSeq; //공고seq
  private long corpNo; //기업 사업자등록번호
  private int positionSeq; // 포지션seq
  private String postingTitle; //공고 제목
  private String postingDetail; //공고 내용
  private String expLevel; // 전체, 신입, 1년차~10년차
  private String postingEndDt; //공고마감일
  private String postingStartDt; //공고시작일
  private int recruitCnt; //모집인원
  private String employType; //인턴, 정규, 계약
  private String workday; //근무요일
  private String workStartTime; //근무 시작시간
  private String workEndTime; //근무 종료시간
  private long salary; //연봉
  private String contStartDt; //계약 시작일
  private String contEndDt; //계약 종료일
  private String eduLevel; //학력조건 (무관, 고졸이상, 초대졸, 대졸, 석사, 박사) 
  private int viewCnt; //공고 조회수
  private String zipcode; //우편번호
  private String roadAddress; //도로명주소
  private String detailAddress; //상세주소
  private String region; //시
  private String district; //구군 
  private String isEnded; //마감 여부 
  
  private CorpDTO corpDTO; //기타 회사 정보 (태그같은거)
  
  private List<Integer> techStackSeqList; //사용 기술 리스트 (자바, Oracle, Javascript, JQuery 등등)
  
  //내 공고 리스트에서 쓰일 변수들 
  private String postSts; //상태: 전체(total), 진행중(ing), 마감(end)
  private String orderBy; //정렬: 전체,시작순(start), 마감순(end), 조회순(viewCnt)
  
  private int totalPostingCnt; //갯수
  private int ingPostingCnt;
  private int endPostingCnt;
  
  private int appCnt; //지원자 수
  private String positionName; //포지션 이름
  private String techNames; //기술스택 이름 나열 
  
  
  //entity 받으면 dto로 변환해주는 생성자.. 만들어두면 편하다. 만들기 귀찮아서 그렇지
  public JobPostingDTO(JobPostingEntity entity) {
	  this.jobPostingSeq = entity.getJobPostingSeq() != null ? entity.getJobPostingSeq() : 0;
	  this.corpNo = entity.getCorpNo() != null ? entity.getCorpNo().getCorpNo() : 0L; // CorpEntity에서 corpNo 추출
	  this.positionSeq = entity.getPositionSeq() != null ? entity.getPositionSeq().getPositionSeq() : 0; // PositionCodeEntity에서 positionSeq 추출
	  this.postingTitle = entity.getPostingTitle();
	  this.postingDetail = entity.getPostingDetail();
	  this.expLevel = entity.getExpLevel();
	  this.postingEndDt = entity.getPostingEndDt();
	  this.postingStartDt = entity.getPostingStartDt();
	  this.recruitCnt = entity.getRecruitCnt() != null ? entity.getRecruitCnt() : 0;
	  this.employType = entity.getEmployType();
	  this.workday = entity.getWorkday();
	  this.workStartTime = entity.getWorkStartTime();
	  this.workEndTime = entity.getWorkEndTime();
	  this.salary = entity.getSalary() != null ? entity.getSalary().longValue() : 0L;
	  this.contStartDt = entity.getContStartDt();
	  this.contEndDt = entity.getContEndDt();
	  this.eduLevel = entity.getEduLevel();
	  this.viewCnt = entity.getViewCnt() != null ? entity.getViewCnt() : 0;
	  this.zipcode = entity.getZipcode();
	  this.roadAddress = entity.getRoadAddress();
	  this.detailAddress = entity.getDetailAddress();
	  this.region = entity.getRegion();
	  this.district = entity.getDistrict();
	  this.isEnded = entity.getIsEnded();
  }
  
	public static JobPostingDTO from(JobPostingEntity entity) {
		return new JobPostingDTO(entity);
	}
  
  
}
