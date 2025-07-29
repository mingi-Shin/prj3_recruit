package kr.co.sist.user.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.sist.admin.review.AdminReviewDTO;
import kr.co.sist.user.dto.ReviewDTO;
import kr.co.sist.user.dto.ReviewSearchDTO;

@Mapper
public interface ReviewMapper {
	
	 /**
	 * 해당 기업의 모든 리뷰 조회
	 * @return
	 */
	 List<ReviewDTO> selectReviewsByCorpNo(Long corpNo);

	 
	 List<ReviewDTO> selectReviewsPage(ReviewSearchDTO searchDTO);

	 
	 /**
    * 해당 기업의 리뷰 총 개수 조회
    */
   int countReviewsByCorpNo(Long corpNo);
	
   /**
    * 리뷰 통계 조회 (평균 평점, 총 리뷰 수)
    */
   ReviewDTO selectReviewStats(Long corpNo);
   
   /**
    * 기업명 조회
    */
   String selectCompanyName(Long corpNo);
   
   /**
    * 리뷰 작성 권한 확인 (해당 기업에 최종합격한 이력이 있는지)
    */
   int checkReviewEligibility(String email, Long corpNo);
   
   /**
    * 이미 리뷰를 작성했는지 확인
    */
   int checkExistingReview(String email, Long corpNo);
   
   /**
    * 리뷰 저장
    */
   int insertReview(ReviewDTO dto);
	List<ReviewDTO> selectReviews(String email);
	
	
	/**
	 * 검색 조건, 페이징, 정렬 조건으로 조회
	 * @param dto 검색, 페이징, 정렬 
	 * @return 
	 */
	List<AdminReviewDTO> selectSearchReviews(Map<String,Object> map);
	
	/**
	 * 검색 조건에 맞는 개수
	 * @param dto 검색조건
	 * @return 총 개수
	 */
	int countSearch(Map<String, Object> map);
	
	
	/**
	 * 체크 삭제
	 * @param map
	 */
	int deleteReview(Map<String,Object> map);
	
 
}