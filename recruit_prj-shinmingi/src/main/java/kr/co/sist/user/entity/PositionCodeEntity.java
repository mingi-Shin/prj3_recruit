package kr.co.sist.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

@Entity
@Table(name="POSITION_CODE")
public class PositionCodeEntity {

	@Id
	@Column(name="position_seq")
	private int positionSeq;
	
	@Column(name="position_name")
	private String positionName;
}
