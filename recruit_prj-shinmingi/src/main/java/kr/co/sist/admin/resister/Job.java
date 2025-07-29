package kr.co.sist.admin.resister;

public enum Job {
	 EMPLOYEE("사원"),
   ASSISTANT_MANAGER("대리"),
   MANAGER("과장"),
   TEAM_LEADER("팀장");
	
	private final String korJobName;
	
	Job(String korJobName) {
		this.korJobName = korJobName;
	}
	
	public String getKorJobName() {
		return korJobName;
	}
}
