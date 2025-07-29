package kr.co.sist.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import kr.co.sist.user.entity.InquiryEntity;

@Repository
public interface InquiryRepository extends JpaRepository<InquiryEntity, Long>, JpaSpecificationExecutor<InquiryEntity> {
    
    // 사용자별 문의 목록 조회 (최신순)
    Page<InquiryEntity> findByEmailOrderByRegsDateDesc(String email, Pageable pageable);
    
    // 카테고리별 문의 목록 조회 (페이징 지원)
    Page<InquiryEntity> findByCategoryOrderByRegsDateDesc(String category, Pageable pageable);

    // 카테고리별 답변 완료된 문의 조회 (FAQ용)
    List<InquiryEntity> findByCategoryAndAnswerStatOrderByRegsDateDesc(String category, String answerStat);
    
    // 카테고리와 답변상태로 문의 조회
    List<InquiryEntity> findByCategoryAndAnswerStat(String category, String answerStat);
    
    // 답변 상태별 문의 조회
    List<InquiryEntity> findByAnswerStat(String answerStat);
    
    // 카테고리별 문의 통계
    @Query("SELECT i.category, COUNT(i) FROM InquiryEntity i GROUP BY i.category")
    List<Object[]> getInquiryStatsByCategory();
    
    // 답변 상태별 문의 통계
    @Query("SELECT i.answerStat, COUNT(i) FROM InquiryEntity i GROUP BY i.answerStat")
    List<Object[]> getInquiryStatsByAnswerStatus();
    
    // 카테고리별 답변 완료된 문의 개수
    @Query("SELECT i.category, COUNT(i) FROM InquiryEntity i WHERE i.answerStat = 'Y' GROUP BY i.category")
    List<Object[]> getFAQCountByCategory();
    
    // 최근 등록된 문의 조회
    List<InquiryEntity> findTop10ByOrderByRegsDateDesc();
    
    // 특정 기간 내 문의 조회
    @Query("SELECT i FROM InquiryEntity i WHERE i.regsDate >= :startDate AND i.regsDate <= :endDate ORDER BY i.regsDate DESC")
    List<InquiryEntity> findInquiriesByDateRange(
        @Param("startDate") java.time.LocalDateTime startDate, 
        @Param("endDate") java.time.LocalDateTime endDate
    );
    
    // 키워드로 문의 검색 (제목 + 내용)
    @Query("SELECT i FROM InquiryEntity i WHERE i.title LIKE %:keyword% OR i.content LIKE %:keyword% ORDER BY i.regsDate DESC")
    Page<InquiryEntity> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}