package kr.co.sist.corp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.sist.corp.dto.CorpEntity;

@Repository
public interface CorpImageRepository extends JpaRepository<CorpEntity, Long> {
    
}