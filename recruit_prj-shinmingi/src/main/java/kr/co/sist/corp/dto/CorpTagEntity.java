package kr.co.sist.corp.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="company_tag")
public class CorpTagEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="company_tag_seq")
	private int companyTagSeq;
	
	@ManyToOne
	@JoinColumn(name="corp_no")
	private CorpEntity corpEntity;
	
	@ManyToOne
	@JoinColumn(name="tag_seq")
	private TagEntity tagEntity;
	
}
