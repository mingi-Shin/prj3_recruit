package kr.co.sist.admin.login.log;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminLoginLogDTO {
	private Integer logSeq;
	private String adminId;
	private String loginTime;
	private String loginIp;
	private Character loginResult;
	private String userAgent;
	private String loginFail;	

}
