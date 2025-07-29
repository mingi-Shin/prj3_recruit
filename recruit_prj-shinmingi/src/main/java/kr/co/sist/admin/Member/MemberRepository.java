package kr.co.sist.admin.Member;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<MemberEntity, String>{
	MemberEntity findByName(String name);
	List<MemberEntity> findByNameContaining(String name);
	MemberEntity findByEmail(String email);
	List<MemberEntity> findByGender(String gender);
	List<MemberEntity> findByactiveStatus(Integer status);
}
