package kr.co.sist.user.dto; 

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JobPostDTO {
	
    private Integer jobPostingSeq;
    private long corpNo;
    private Integer positionSeq;
    private String postingTitle;
    private String postingDetail;
    private String expLevel;
    private String postingEndDt;
    private String postingStartDt;
    private Integer recruitCnt;
    private String employType;
    private String workday;
    private String workStartTime;
    private String workEndTime;
    private Integer salary;
    private String contStartDt;
    private String contEndDt;
    private String eduLevel;
    private Integer viewCnt;
    private String zipcode;
    private String region;
    private String district;
    private String roadAddress;
    private String detailAddress;
    private String positionName;  
    private String stackName;
    private List<String> techStacks;
    private String isEnded;
    private String corpCreatedAt;
    private String corpImg;
  	private String corpLogo;
  	private long corpAvgSal;
    private String corpNm;          
    private String corpInfo;        
    private String corpUrl;     
    private String techNames;
    
    // D-day 계산을 위한 필드 추가
    private Integer daysRemaining;
    
    
    public void addTechStack(String stackName) {
      if (this.techStacks == null) {
          this.techStacks = new ArrayList<>();
      }
      if (stackName != null && !stackName.equals("기술 스택 없음")) {
          this.techStacks.add(stackName);
      }
  }
    
    // D-day 문자열 반환 메소드
    public String getDdayDisplay() {
        if (daysRemaining == null) {
            return "";
        }
        if (daysRemaining == 0) {
            return "D-day";
        } else if (daysRemaining > 0) {
            return "D-" + daysRemaining;
        } else {
            return "마감";
        }
    }
    
    
}
