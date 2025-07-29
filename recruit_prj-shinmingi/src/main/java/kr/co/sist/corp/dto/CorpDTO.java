package kr.co.sist.corp.dto;
 
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorpDTO {

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
	
	private List<TagDTO> tagList; 
	
	//CorpEntity를 DTO로 변환해주는 메서드
	public CorpDTO(CorpEntity ce) {
		this.corpNo = ce.getCorpNo();
		this.corpNm = ce.getCorpNm();
		this.corpInfo =ce.getCorpInfo();
		this.corpUrl = ce.getCorpUrl();
		this.corpLogo = ce.getCorpLogo();
		this.corpImg = ce.getCorpImg();
		this.corpAvgSal =ce.getCorpAvgSal();
		this.corpAnnualRevenue = ce.getCorpAnnualRevenue();
		this.corpCreatedAt = ce.getCorpCreatedAt();
		this.corpEmpCnt = ce.getCorpEmpCnt();
		this.bizCert =ce.getBizCert();
		this.corpAiActive = ce.getCorpAiActive();
		this.industry = ce.getIndustry();
		this.companySize = ce.getCompanySize();
		this.corpCeo = ce.getCorpCeo();
	}
	
	public static CorpDTO from(CorpEntity entity) {
		return new CorpDTO(entity);
	}
	
}
