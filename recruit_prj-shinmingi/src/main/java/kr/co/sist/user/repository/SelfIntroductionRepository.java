package kr.co.sist.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.sist.user.entity.SelfIntroductionEntity;

public interface SelfIntroductionRepository extends JpaRepository<SelfIntroductionEntity, Integer> {

}
