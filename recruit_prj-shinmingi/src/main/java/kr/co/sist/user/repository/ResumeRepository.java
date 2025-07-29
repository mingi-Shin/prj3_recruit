package kr.co.sist.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.sist.user.entity.ResumeEntity;

public interface ResumeRepository extends JpaRepository<ResumeEntity, Integer>  {

}
