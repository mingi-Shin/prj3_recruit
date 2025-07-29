package kr.co.sist.corp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.sist.corp.dto.CorpEntity;

import java.util.List;

@Repository
public interface CorpEditRepository extends JpaRepository<CorpEntity, Long> {

    // 기업명으로 검색
    CorpEntity findByCorpNm(String corpNm);

    // 사업자등록번호로 검색
    CorpEntity findByBizCert(String bizCert);

    // 업종별 기업 조회
    List<CorpEntity> findByIndustry(String industry);

    // 기업 규모별 조회
    List<CorpEntity> findByCompanySize(String companySize);

    // AI 활용 여부별 조회
    List<CorpEntity> findByCorpAiActive(String corpAiActive);

    // 직원 수 범위로 검색
    @Query("SELECT c FROM CorpEntity c WHERE c.corpEmpCnt >= :minEmp AND c.corpEmpCnt <= :maxEmp")
    List<CorpEntity> findByEmployeeCountRange(@Param("minEmp") Integer minEmp, @Param("maxEmp") Integer maxEmp);

    // 연봉 범위로 검색
    @Query("SELECT c FROM CorpEntity c WHERE c.corpAvgSal >= :minSal AND c.corpAvgSal <= :maxSal")
    List<CorpEntity> findBySalaryRange(@Param("minSal") Long minSal, @Param("maxSal") Long maxSal);

    // 기업명 부분 검색
    @Query("SELECT c FROM CorpEntity c WHERE c.corpNm LIKE %:keyword%")
    List<CorpEntity> findByCorpNameContaining(@Param("keyword") String keyword);

    // 기업 정보 부분 검색
    @Query("SELECT c FROM CorpEntity c WHERE c.corpInfo LIKE %:keyword%")
    List<CorpEntity> findByCorpInfoContaining(@Param("keyword") String keyword);

    // 설립일 범위로 검색
    @Query("SELECT c FROM CorpEntity c WHERE c.corpCreatedAt >= :startDate AND c.corpCreatedAt <= :endDate")
    List<CorpEntity> findByCreatedAtRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    // 로고가 있는 기업 조회
    @Query("SELECT c FROM CorpEntity c WHERE c.corpLogo IS NOT NULL AND c.corpLogo != ''")
    List<CorpEntity> findCorpsWithLogo();

    // 웹사이트가 있는 기업 조회
    @Query("SELECT c FROM CorpEntity c WHERE c.corpUrl IS NOT NULL AND c.corpUrl != ''")
    List<CorpEntity> findCorpsWithWebsite();
}