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
@Table(name = "LINK")
public class LinkEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int linkSeq;
	
	private int resumeSeq;
	private String githubUrl, notionUrl, blogUrl;
}
