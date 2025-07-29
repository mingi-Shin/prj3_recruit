package kr.co.sist.user.dto;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Optional;

import kr.co.sist.corp.dto.CorpEntity;
import kr.co.sist.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private String email;
	private Long corpNo;
	private String password;
	private String name;
	private String role;
	private String phone;
	private Date regDt;
	private Integer loginFailCnt;
	private Integer isLocked;
	private Date lockEndDt;
	private Date pwChangeDt;
	private Date lastLoginDt;
	private String lastLoginIp;
	private String zipcode;
	private String roadAddress;
	private String detailAddress;
	private Date birth;
	private String gender;
	private Integer pwResetRequired;
	private String profileImage;
	private Integer activeStatus;
	
	//userEntity를 DTO로 변환해주는 메서드 
  public UserDTO(UserEntity userEntity) {
  	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
  	SimpleDateFormat sdfBirth = new SimpleDateFormat("yyyy-mm-dd");
  	
    this.email = userEntity.getEmail();
    //this.corpNo = userEntity.getCorpEntity().getCorpNo() != null ? userEntity.getCorpEntity().getCorpNo() : null ;
    this.corpNo = Optional.ofNullable(userEntity.getCorpEntity()).map(CorpEntity :: getCorpNo).orElse(null);
    this.password = userEntity.getPassword();
    this.name = userEntity.getName();
    this.role = userEntity.getRole();
    this.phone = userEntity.getPhone();
   
    if(userEntity.getRegDt() != null && !userEntity.getRegDt().isBlank()) {
    	try {
    		this.regDt = new java.sql.Date(sdf.parse(userEntity.getRegDt()).getTime());
			} catch (Exception e) {
				e.printStackTrace(); // 필요에 따라 예외 처리
		    this.regDt = null;
			}
    }
    
    this.loginFailCnt = userEntity.getLoginFailCnt();
    this.isLocked = userEntity.getIsLocked();
    
    if(userEntity.getLockEndDt() != null && !userEntity.getLockEndDt().isBlank()) {
    	try {
    		this.lockEndDt = new java.sql.Date(sdf.parse(userEntity.getLockEndDt()).getTime());
    	} catch (Exception e) {
    		e.printStackTrace(); // 필요에 따라 예외 처리
    		this.lockEndDt = null;
    	}
    }
    if(userEntity.getPwChangeDt() != null && !userEntity.getPwChangeDt().isBlank()) {
    	try {
    		this.pwChangeDt = new java.sql.Date(sdf.parse(userEntity.getPwChangeDt()).getTime());
    	} catch (Exception e) {
    		e.printStackTrace(); // 필요에 따라 예외 처리
    		this.pwChangeDt = null;
    	}
    }
    
    if(userEntity.getLastLoginDt() != null && !userEntity.getLastLoginDt().isBlank()) {
    	try {
    		this.lastLoginDt = new java.sql.Date(sdf.parse(userEntity.getLastLoginDt()).getTime());
    	} catch (Exception e) {
    		e.printStackTrace(); // 필요에 따라 예외 처리
    		this.lastLoginDt = null;
    	}
    }
    
    this.lastLoginIp = userEntity.getLastLoginIp();
    this.zipcode = userEntity.getZipcode();
    this.roadAddress = userEntity.getRoadAddress();
    this.detailAddress = userEntity.getDetailAddress();
    if(userEntity.getBirth() != null && !userEntity.getBirth().isBlank()) {
    	try {
    		this.birth = new java.sql.Date(sdfBirth.parse(userEntity.getBirth()).getTime());
    	} catch (Exception e) {
    		e.printStackTrace(); // 필요에 따라 예외 처리
    		this.birth = null;
    	}
    }
    this.gender = userEntity.getGender();
    this.pwResetRequired = userEntity.getPwResetRequired();
    this.profileImage = userEntity.getProfileImage();
    this.activeStatus = userEntity.getActiveStatus();
  }
  
  
  public static UserDTO from(UserEntity entity) {
    return new UserDTO(entity);
  }
}
