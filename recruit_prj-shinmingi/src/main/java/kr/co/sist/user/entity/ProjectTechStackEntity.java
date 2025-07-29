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
@Table(name = "PROJECT_TECH_STACK")
public class ProjectTechStackEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int projectTechStackSeq;
	
	private int projectSeq, techStackSeq;
}
