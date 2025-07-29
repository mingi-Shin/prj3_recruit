package kr.co.sist.admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminRepository extends JpaRepository<AdminEntity,String>, JpaSpecificationExecutor<AdminEntity>{
		
	@Query("SELECT a FROM AdminEntity a WHERE " +
      "(:keyword IS NULL OR :keyword = '' OR " +
      "(:searchType = '이름' AND a.name LIKE %:keyword%) OR " +
      "(:searchType = '이메일' AND a.adminId LIKE %:keyword%) OR " +
      "(:searchType = '전체' AND (a.name LIKE %:keyword% OR a.adminId LIKE %:keyword%))" +
      ") " +
      "AND (:dept IS NULL OR a.dept = :dept) " +
      "AND (:job IS NULL OR a.job = :job) " +
      "AND (:stat IS NULL OR a.stat = :stat) " +
      "ORDER BY CASE a.stat WHEN '승인대기' THEN 1 WHEN '승인됨' THEN 2 WHEN '탈퇴' THEN 3 ELSE 4 END ASC, " +
      "CASE WHEN a.stat = '승인대기' THEN a.approvalRequestDate ELSE NULL END ASC")
Page<AdminEntity> findAdminsWithCustomOrder(
      @Param("searchType") String searchType,
      @Param("keyword") String keyword,
      @Param("dept") String dept,
      @Param("job") String job,
      @Param("stat") String stat,
      Pageable pageable);

}
