package kr.co.sist.admin.Member;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.sist.admin.dashboard.DashboardMapper;
import kr.co.sist.user.dto.UserDTO;

@Service
public class MemberService {
	@Autowired
	private MemberRepository mer;
	
	 @Autowired
   private MemberMapper memberMapper;
	
	public List<MemberEntity> searchAll() {
		return mer.findAll();
	}
	
	public MemberEntity searchNameMember(String name) {
		return mer.findByName(name);
	}
	public MemberEntity searchEmailMember(String email) {
		return mer.findByEmail(email);
	}
	
	public List<MemberEntity> searchGenderMember(String gender) {
		return mer.findByGender(gender);
	}
	
	public List<MemberEntity> searchStatusMember(String status) {
		return mer.findByactiveStatus(Integer.parseInt(status));
	}
	
	public List<MemberEntity> searchAll2() {
		return memberMapper.selectMember();
	}
	
	public List<MemberEntity> searchMember(String email,String name, String gender, Integer status,String type) {
		return memberMapper.searchMember(email,name, gender, status,type);
	}
	
	public void sanctionMember(String email) {
		memberMapper.sanctionMember(email);
		return;
	}
	public void sanctionCancel(String email) {
		memberMapper.sanctionCancel(email);
		return;
	}
}
