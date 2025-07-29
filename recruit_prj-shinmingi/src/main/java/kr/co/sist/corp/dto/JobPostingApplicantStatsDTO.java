package kr.co.sist.corp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingApplicantStatsDTO {

	private int jobPostingSeq;
	private String jobPostingTitle;
	
	private int totalApplicantCount;
	
	private int newEmployee;
	private int oneYearEmployee;
	private int threeYearEmployee;
	private int fiveYearEmployee;
	
	private int maleCount;
	private int femaleCount;
	private double maleRatio;
	private double femaleRatio;
	
	private int ageGroup20s;
	private int ageGroup30s;
	private int ageGroup40s;
	private int ageGroup50s;
	private int ageGroup60s;
	
	private int highSchoolCount;
	private int associateDegreeCount;
	private int bachelorDegreeCount;
	private int masterDegreeCount;
	private int doctorateDegreeCount;
	
	private int hasToeicCount;
	private int hasToeflCount;
	private int hasTepsCount;
	private int hasToeicSpeakingCount;
	private int hasOpicCount;
	private int hasJptCount;
	private int hasHskCount;
	
	private int hasJcgCount;
	private int hasSqldCount;
	private int hasLinuxCount;
	private int hasOcpCount;
	private int hasAdspCount;
	
	private int has0CertCount;
	private int has1CertCount;
	private int has2CertCount;
	private int has3CertCount;
	private int has4OrMoreCount;
	
	private int sentProjectCount;
	private int noSentProjectCount;
	private double sentProjectRatio;
	
	

	
	
	
}
