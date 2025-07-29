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
@Table(name = "RESUME_TECH_STACK")
public class ResumeTechStackEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int resumeTechStackSeq;
	
	private int techStackSeq, resumeSeq;
}
