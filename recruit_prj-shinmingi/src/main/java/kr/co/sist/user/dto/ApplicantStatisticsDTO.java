package kr.co.sist.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
//지원자 개별 통계용 DTO
public class ApplicantStatisticsDTO {

	private int completed = 0;   // 지원완료 (passStage == 0)
	private int docPassed = 0;   // 서류 통과 (passStage == 1)
	private int passed = 0;      // 최종합격 (passStage == 2)
	private int failed = 0;      // 불합격 (passStage == 3)
}
