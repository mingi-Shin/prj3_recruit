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
@Table(name = "EDUCATION_HISTORY")
public class EducationHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int educationHistorySeq;
	
	private int resumeSeq, indexNum;
	private String schoolName, department, educationType, admissionDate, graduateDate;
	private double grade, standardGrade;
}
