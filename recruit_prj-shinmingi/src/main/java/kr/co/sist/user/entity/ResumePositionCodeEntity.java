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
@Table(name = "RESUME_POSITION_CODE")
public class ResumePositionCodeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int resumePositionSeq;
	
	private int resumeSeq, positionSeq;
}
