package kr.co.sist.corp.dto;

import lombok.Data;

@Data
public class TalentPoolDTO {

    private Long resumeSeq;
    private Long corpNo;
    private String isScrapped;
    private String scrappedAt;
    private String readAt;
    
    private String name;
    private String email;
    private String title;
    private String gender;
    private String birth;
    private String shortAddress;
    private String totalCareer;
    private String finalEducation;
    private String educationType;
    private String desiredPositions;
    private String techStacks;
    private String certifications;
    private String latestDate;
		public String ProfileImg;
    
}