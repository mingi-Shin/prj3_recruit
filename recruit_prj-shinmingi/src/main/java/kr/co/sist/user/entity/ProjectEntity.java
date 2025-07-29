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
@Table(name = "PROJECT")
public class ProjectEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int projectSeq;
	
	private int resumeSeq;
	private String projectName;
	private String projectContent;
	private String startDate;
	private String endDate;
	private String releaseStatus;
	private String repositoryLink;

}
