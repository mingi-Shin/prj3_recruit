package kr.co.sist.corp.dto;

import java.util.List;

import kr.co.sist.user.dto.AdditionalInfoDTO;
import kr.co.sist.user.dto.CareerDTO;
import kr.co.sist.user.dto.EducationHistoryDTO;
import kr.co.sist.user.dto.LinkDTO;
import kr.co.sist.user.dto.PositionCodeDTO;
import kr.co.sist.user.dto.ProjectDTO;
import kr.co.sist.user.dto.SelfIntroductionDTO;
import kr.co.sist.user.dto.TechStackDTO;
import kr.co.sist.user.dto.UserDTO;
import lombok.Data;

@Data
public class ResumeDetailDTO {
  private Long resumeSeq;
  private String title;
  private String image;
  private String introduction;
  private String createdAt;
  private String updatedAt;
  private String careerType;
  private String isPublic;
  private String isScrapped;

  private UserDTO member;  // email, name, phone, birth, profile_image 등

  private List<PositionCodeDTO> positions;  // positionName
  private List<TechStackDTO> skills;    // stackName

  private List<EducationHistoryDTO> educations; // 학력 정보
  private List<CareerDTO> careers;       // 경력 정보
  private List<ProjectDTO> projects;     // 프로젝트 정보, projectSkills 포함
  private List<AdditionalInfoDTO> additionals; // 기타사항
  private List<SelfIntroductionDTO> introductions; // 자기소개서
  private LinkDTO links;                    // Github, Notion, Blog 링크
}
