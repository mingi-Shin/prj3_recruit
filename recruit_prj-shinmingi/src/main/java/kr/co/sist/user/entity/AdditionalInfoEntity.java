package kr.co.sist.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

@Entity
@Table(name = "ADDITIONAL_INFO")
public class AdditionalInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int additionalInfoSeq;
	
	private int resumeSeq;
	private String activityName, recordType, relatedAgency, detailContent, endDate;
	
}
