package kr.co.sist.admin.login.log;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminLoginLogMapper {
	
	public void insertLoginLog(Map<String, Object> param);

	public void increaseLoginFailCount(String adminId);

	public int selectLoginFailCount(String adminId);

	public void lockAccount(String adminId);

	public void resetLoginFailCount(String adminId);
	
	public List<AdminLoginLogDTO> selectTop5LoginLogs(String adminId);
}
