package kr.co.sist.corp.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kr.co.sist.user.entity.PositionCodeEntity;
import lombok.Data;

@Entity
@Data
@Table(name="JOB_POSTING")
public class JobPostingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="JOB_POSTING_SEQ")
	private Integer jobPostingSeq;
	
  // 기업은 여러 공고를 가질 수 있음 (1:N)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CORP_NO")
	private CorpEntity corpNo;
	
  // 포지션은 여러 공고에서 사용될 수 있음 (N:1)
  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POSITION_SEQ")
	private PositionCodeEntity positionSeq;

  @Column(name = "POSTING_TITLE", length = 50)
  private String postingTitle;

  @Column(name = "POSTING_DETAIL", length = 4000)
  private String postingDetail;

  @Column(name = "EXP_LEVEL", length = 50)
  private String expLevel;

  @Column(name = "POSTING_END_DT", length = 20)
  private String postingEndDt;

  @Column(name = "POSTING_START_DT", length = 20)
  private String postingStartDt;

  @Column(name = "RECRUIT_CNT")
  private Integer recruitCnt;

  @Column(name = "EMPLOY_TYPE", length = 30)
  private String employType;

  @Column(name = "WORKDAY", length = 50)
  private String workday;

  @Column(name = "WORK_START_TIME", length = 30)
  private String workStartTime;

  @Column(name = "WORK_END_TIME", length = 30)
  private String workEndTime;

  @Column(name = "SALARY")
  private Integer salary;

  @Column(name = "CONT_START_DT", length = 20)
  private String contStartDt;

  @Column(name = "CONT_END_DT", length = 20)
  private String contEndDt;

  @Column(name = "EDU_LEVEL", length = 30)
  private String eduLevel;

  @Column(name = "VIEW_CNT")
  private Integer viewCnt;

  @Column(name = "ZIPCODE", length = 30)
  private String zipcode;

  @Column(name = "ROAD_ADDRESS", length = 50)
  private String roadAddress;

  @Column(name = "REGION", length = 30)
  private String region;

  @Column(name = "DISTRICT", length = 30)
  private String district;

  @Column(name = "DETAIL_ADDRESS", length = 50)
  private String detailAddress;
  
  private String isEnded;
	
}
