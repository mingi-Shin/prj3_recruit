package kr.co.sist.corp.dto;

import lombok.Data;

@Data
public class ResumeScrapDTO {

    private Long resumeSeq;
    private Long corpNo;
    private String isScrapped;
    private String scrapped_at;
    
}