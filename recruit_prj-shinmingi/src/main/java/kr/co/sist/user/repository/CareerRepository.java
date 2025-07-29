package kr.co.sist.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.sist.user.entity.CareerEntity;

public interface CareerRepository extends JpaRepository<CareerEntity, Integer> {

}
