package kr.co.sist.admin.faq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kr.co.sist.admin.AdminEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name="FAQ")
public class FaqEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="FAQ_SEQ")
	private Integer faqSeq;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="ADMIN_ID", referencedColumnName = "ADMIN_ID")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private AdminEntity adminId;
	@Column(name="TITLE")
	private String title;
	@Column(name="CONTENT")
	private String content;
	@Column(name="REGS_DATE")
	private String regs_date;
	@Column(name="modify_DATE")
	private String modify_date;
	
}
