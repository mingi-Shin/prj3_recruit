package kr.co.sist.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.sist.user.entity.PositionCodeEntity;

public interface PositionCodeRepository extends JpaRepository<PositionCodeEntity, Integer> {

}
