package kr.co.sist.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {
	private long corpNo; 
	private String corpNm;
	private String corpInfo;
	private String corpUrl;
	private String corpLogo;
	private String corpImg;
	private long corpAvgSal;
	private long corpAnnualRevenue;
	private String corpCreatedAt;
	private int corpEmpCnt;
	private String bizCert;
	private String corpAiActive;
	private String industry;
	private String companySize;
	private String corpCeo; 
	
	private String uploadedFileName; // 저장된 파일 이름
	
	

}
