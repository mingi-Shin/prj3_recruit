package kr.co.sist.admin.Member;

import java.sql.Date;

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
@Table(name="member")
public class MemberEntity {
	@Id
	@Column(name="EMAIL")
	private String email;
	
	@Column(name="CORP_NO")
	private Long corp_no;
	
	@Column(name="PASSWORD")
	private String password;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="ROLE")
	private String role;
	
	@Column(name="PHONE")
	private String phone;
	
	@Column(name="REG_DT")
	private String regDt;
	
	@Column(name="LOGIN_FAIL_CNT")
	private Integer login_fail_cnt;
	
	@Column(name="IS_LOCKED")
	private Integer is_locked;
	
	@Column(name="LOCK_END_DT")
	private String lock_end_dt;
	
	@Column(name="PW_CHANGE_DT")
	private String pw_change_dt;
	
	@Column(name="LAST_LOGIN_DT")
	private String last_login_dt;
	
	@Column(name="LAST_LOGIN_IP")
	private String last_login_ip;
	
	@Column(name="ZIPCODE")
	private String zipcode;

	@Column(name="ROAD_ADDRESS")
	private String road_address;
	
	@Column(name="DETAIL_ADDRESS")
	private String detail_address;
	
	@Column(name="BIRTH")
	private String birth;
	
	@Column(name="GENDER")
	private String gender;
	
	@Column(name="PW_RESET_REQUIRED")
	private Integer pw_reset_required;
	
	@Column(name="PROFILE_IMAGE")
	private String profile_image;
	
	@Column(name="ACTIVE_STATUS")
	private Integer activeStatus;
	
}
