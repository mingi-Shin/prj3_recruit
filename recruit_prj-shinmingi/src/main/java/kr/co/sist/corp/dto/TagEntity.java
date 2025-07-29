package kr.co.sist.corp.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="tag")
public class TagEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="tag_seq")
	private Integer tagSeq;
	
	@Column(name="tag_type")
	private String tagType;
	@Column(name="tag_detail")
	private String tagDetail;
	@Column(name="is_active")
	private String isActive;
	
}
