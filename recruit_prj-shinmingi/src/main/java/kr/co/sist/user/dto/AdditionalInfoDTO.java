package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Alias("userAdditionalInfoDTO")
@Getter
@Setter
@ToString
public class AdditionalInfoDTO {

	private int additionalInfoSeq;
	private int resumeSeq;
	private String activityName, recordType, relatedAgency, detailContent, endDate;
	
}
