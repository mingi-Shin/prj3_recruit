package kr.co.sist.admin.Member;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.sist.user.dto.UserDTO;

@Mapper
public interface MemberMapper {
	List<MemberEntity> selectMember();
	List<MemberEntity> searchMember(String email,String name, String gender, Integer status,String type);
	void sanctionMember(String email);
	void sanctionCancel(String email);
}