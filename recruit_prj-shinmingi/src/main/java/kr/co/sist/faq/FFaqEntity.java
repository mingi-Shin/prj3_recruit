package kr.co.sist.faq;

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
@Table(name="faq")
public class FFaqEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name=" faq_Seq ") // ✅ 실제 컬럼명으로 수정
    private Integer  faqSeq ;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ADMIN_ID", referencedColumnName = "ADMIN_ID")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AdminEntity adminId;
    
    @Column(name="TITLE")
    private String title;
    
    @Column(name="CONTENT")
    private String content;
    
    @Column(name="REGS_DATE")
    private String regsDate;
    
    @Column(name="MODIFY_DATE")
    private String modifyDate;
    
    // ✅ usertype 컬럼이 실제로 없으므로 제거하거나 추가 필요
    // @Column(name="usertype")
    // private String usertype;
}