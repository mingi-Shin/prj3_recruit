package kr.co.sist.corp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllApplicantInfoDTO {

  // 개인정보
  private String name;                    // 이름
  private String email;                   // 이메일
  private String roadAddress;             // 도로명주소
  private String birth4;                  // 출생년도 (4자리)
  private String gender;                  // 성별
  
  // 경력정보
  private String exp;                     // 직무/포지션
  private String expYear;                 // 경력년수 (1년 미만, 1~3년, 3~5년, 5년이상, 신입)
  
  // 학력정보
  private String schoolName;              // 학교명
  private String major;                   // 전공 (department)
  private String grade;                   // 학점
  private String educationType;           // 학력구분
  private String graduateDate;            // 졸업일
  
  // 어학 자격증 (1: 있음, 0: 없음)
  private Integer hasToeic;               // 토익
  private Integer hasTofel;               // 토플
  private Integer hasTeps;                // 텝스
  private Integer hasToeicSpeaking;       // 토익스피킹
  private Integer hasOpic;                // 오픽
  private Integer hasJpt;                 // JPT
  private Integer hasHsk;                 // HSK (중국어)
  
  // IT 자격증 (1: 있음, 0: 없음)
  private Integer hasEngineer;            // 정보처리기사
  private Integer hasSqld;                // SQLD
  private Integer hasLinux;               // 리눅스마스터
  private Integer hasOcp;                 // OCP
  private Integer hasAdsp;                // ADSP
  
  // 프로젝트 경험
  private Integer hasProject;             // 프로젝트 경험 유무 (1: 있음, 0: 없음)
  
  // 자격증 개수
  private String certCount;               // 자격증 개수 (0개, 1개, 2개, 3개, 4개이상)
  
}
