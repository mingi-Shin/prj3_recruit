package kr.co.sist.user.dto;

import java.util.List;

import kr.co.sist.user.entity.ResumeEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResumeResponseDTO {

	private ResumeEntity resume;
	private LinkDTO links;
	private List<ResumePositionCodeDTO> positions;
	private List<ResumeTechStackDTO> skills;
	private List<EducationHistoryDTO> educations;
	private List<CareerDTO> careers;
	private List<ProjectDTO> projects;
	private List<AdditionalInfoDTO> additionals;
	private List<SelfIntroductionDTO> introductions;

}
