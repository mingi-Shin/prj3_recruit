package kr.co.sist.corp.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="company")
public class CorpEntity {
	
	@Id
	@Column(name="corp_no")
	private Long corpNo;
	
	@Column(name="corp_nm")
	private String corpNm;
	@Column(name="corp_info")
	private String corpInfo;
	@Column(name="corp_url")
	private String corpUrl;
	@Column(name="corp_logo")
	private String corpLogo;
	@Column(name="corp_img")
	private String corpImg;
	@Column(name="corp_avg_sal")
	private Long corpAvgSal;
	@Column(name="corp_annual_revenue")
	private Long corpAnnualRevenue;
	@Column(name="corp_created_at")
	private String corpCreatedAt;
	@Column(name="corp_emp_cnt")
	private Integer corpEmpCnt;
	@Column(name="biz_cert")
	private String bizCert;
	@Column(name="corp_ai_active")
	private String corpAiActive;
	@Column(name="industry")
	private String industry;
	@Column(name="company_size")
	private String companySize;
	@Column(name="corp_ceo")
	private String corpCeo;
	
}
