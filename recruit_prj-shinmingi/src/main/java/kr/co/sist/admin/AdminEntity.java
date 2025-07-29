package kr.co.sist.admin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name="ADMIN")
public class AdminEntity {
	@Id
	@Column(name="ADMIN_ID")
	private String adminId;
	@Column(name="PASSWORD")
	private String password;
	@Column(name="NAME")
	private String name;
	@Column(name="DEPT")
	private String dept;
	@Column(name="JOB")
	private String job;
	@Column(name="APPROVAL_DATE")
	private String approvalDate;
	@Column(name="APPROVAL_REQUEST_DATE")
	private String approvalRequestDate;
	@Column(name="STAT")
	private String stat;
	@Column(name="LAST_LOGIN_TIME")
	private String lastLoginTime;
	@Column(name="TEL")
	private String tel;
	@Column(name="DEPT_ROLE")
	private String deptRole;
	@Column(name="JOB_ROLE")
	private String jobRole;
	@Column(name="RESIGNATION_DATE")
	private String resignationDate;
	
	
	/**
	 * prePersit : JPA 엔티티의 라이프사이클 콜백(Lifecycle Callback) = 엔티티가 처음으로 저장(save, insert)되기 직전에 자동으로 호출되는 메서드
	 */
	@PrePersist
	public void prePersist() {
	    if (approvalRequestDate == null) {
	        approvalRequestDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	    }
	    if (stat == null) {
	        stat = "승인대기";
	    }
	    if (deptRole == null && dept != null) {
	      deptRole = "ROLE_" + dept.toUpperCase();
	    }
	    if (jobRole == null && job != null) {
	        jobRole = "ROLE_" + job.toUpperCase();   
	    }
	}

	
//AdminEntity.java
public static AdminEntity from(AdminDTO dto, BCryptPasswordEncoder encoder) {
   AdminEntity entity = new AdminEntity();
   entity.setAdminId(dto.getAdminId());
   entity.setPassword(encoder.encode(dto.getPassword()));  // 비밀번호 암호화 포함
   entity.setName(dto.getName());
   entity.setDept(dto.getDept());
   entity.setJob(dto.getJob());
   entity.setTel(dto.getTel());
   return entity;
}

	

}
