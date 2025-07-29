package kr.co.sist.faq;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FFaqRepository extends JpaRepository<FFaqEntity, Integer> {
    // ADMIN_ID가 있는 것만 FAQ로 간주 (관리자가 등록한 것)
    @Query("SELECT f FROM FFaqEntity f WHERE f.adminId IS NOT NULL")
    List<FFaqEntity> findAllFaqs();
    
    // 또는 제목이나 내용에서 사용자/기업 구분
    @Query("SELECT f FROM FFaqEntity f WHERE f.adminId IS NOT NULL AND (f.title LIKE %:keyword% OR f.content LIKE %:keyword%)")
    List<FFaqEntity> findFaqsByKeyword(@Param("keyword") String keyword);
}