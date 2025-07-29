package kr.co.sist.login;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCorpDTO {

  private Long corpNo;
  private String upfileName;
  private String corpNm;
  private String corpCeo;
  private String zipcode;
  private String roadAddress;
  private String detailAddress;
  private String industry;
  private Long corpAnnualRevenue;
  private Long corpAvgSal;
  private String corpCreatedAt;
  private Integer corpEmpCnt;
  private String email;
  private String password;
  private String phone;
  private String name;
  private String role;
  private Date regDt;
  private Integer loginFailCnt;
  private Integer isLocked;
  private Date lockEndDt;
  private Date pwChangeDt;
  private Date lastLoginDt;
  private String lastLoginIp;
  private Integer pwResetRequired;
  private Integer activeStatus;

}
