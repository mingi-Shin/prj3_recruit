package kr.co.sist.admin.corp;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.sist.corp.dto.CorpEntity;


public interface CorpAdminRepository extends JpaRepository<CorpEntity, Long>{
	//Optional<MemberEntity> findByName(String name);
	List<CorpEntity> findByCorpNmContaining(String name);
	List<CorpEntity> findByCorpNo(Long num);
}
