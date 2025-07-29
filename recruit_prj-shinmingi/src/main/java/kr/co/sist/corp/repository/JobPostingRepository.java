package kr.co.sist.corp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.sist.corp.dto.JobPostingEntity;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPostingEntity, Integer>{
// JPA는 findBy, countBy, existsBy 등 키워드로 시작하는 메서드 이름을 분석해서 자동 쿼리를 만듬
	
	//사업자등록번호로 해당 기업의 공고 목록 가져오기
	public List<JobPostingEntity> findByCorpNo_CorpNo(Long corpNo);
	
	//사업자번호와 포스팅 번호로 공고 하나 가져오기 
	public JobPostingEntity findByCorpNo_CorpNoAndJobPostingSeq(Long corpNo, int jobPostingSeq);
	
	
	
	
}
