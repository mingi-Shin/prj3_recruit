package kr.co.sist.admin.resister;

public enum Dept {
  CUSTOMER_SERVICE("고객센터팀"),
  MEMBER_MANAGEMENT("회원관리팀"),
  CORPORATE("기업관리팀");

	private final String korJobName;
	
	Dept(String korJobName) {
		this.korJobName = korJobName;
	}
	
	public String getKorJobName() {
		return korJobName;
	}
}
