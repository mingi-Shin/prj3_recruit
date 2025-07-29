package kr.co.sist.user.service;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import kr.co.sist.user.dto.ReviewDTO;
import kr.co.sist.user.dto.ReviewSearchDTO;
import kr.co.sist.user.mapper.ReviewMapper;
@Service
public class ReviewService {
	
	@Autowired
	private ReviewMapper reviewMapper;
	
	
	/**
	 * 해당 기업의 모든 리뷰 조회
	 * @param email
	 * @return
	 */
	public List<ReviewDTO> getReviews(Long corpNo){
		
		return reviewMapper.selectReviewsByCorpNo(corpNo);
	}
	
	/**
	 * 페이징된 리뷰 목록 조회
	 * @param searchDTO
	 * @return
	 */
	public List<ReviewDTO> getReviewsWithPaging(ReviewSearchDTO searchDTO) {
		// 전체 리뷰 수 조회 (int를 long으로 변환)
		int totalCount = reviewMapper.countReviewsByCorpNo(searchDTO.getCorpNo());
		
		// 디버깅용 로그 추가
		System.out.println("전체 리뷰 수: " + totalCount);
		System.out.println("현재 페이지: " + searchDTO.getPage());
		System.out.println("페이지 사이즈: " + searchDTO.getSize());
		System.out.println("오프셋: " + searchDTO.getOffset());
		
		// 페이징 정보 계산 (int를 long으로 변환하여 전달)
		searchDTO.calculatePageInfo((long) totalCount);
		
		// 페이징된 리뷰 목록 조회
		List<ReviewDTO> result = reviewMapper.selectReviewsPage(searchDTO);
		System.out.println("조회된 리뷰 수: " + (result != null ? result.size() : 0));
		
		return result;
	}
	
	
	/**
	 * 리뷰 평균 평점, 총 리뷰 수
	 * @param corpNo
	 * @return
	 */
	public ReviewDTO getRating(Long corpNo) {
		
		ReviewDTO stats=reviewMapper.selectReviewStats(corpNo);
		if(stats == null) {
			
			stats=new ReviewDTO();
			stats.setAvgRating(0.0);
			stats.setTotalReviews(0);
			
		}
		
		return stats;
	}
	
	 /**
	 * 기업명 조회
	 * @param corpNo
	 * @return
	 */
	public String getCompanyName(Long corpNo) {
     return reviewMapper.selectCompanyName(corpNo);
 }
	
	
	/**
	 *  리뷰 작성 권한 확인 (해당 기업에 최종합격한 이력이 있는지)
	 * @param email
	 * @param corpNo
	 * @return
	 */
	public boolean checkReviewEligibility(String email, Long corpNo) {
	   int count = reviewMapper.checkReviewEligibility(email, corpNo);
     return count > 0;
	}
	
	 /**
   * 이미 리뷰를 작성했는지 확인
   */
  public boolean hasUserReviewed(String email, Long corpNo) {
      int count = reviewMapper.checkExistingReview(email, corpNo);
      return count > 0;
  }
	
	/**
	 * 리뷰 저장
	 * @param dto
	 * @return
	 */
	public boolean insertReview(ReviewDTO dto) {
	
	try {	
		
    // 현재 시간을 문자열로 설정
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dto.setCreatedAt(sdf.format(new Date()));
		
		int result=reviewMapper.insertReview(dto);
		return result > 0;
		
		
	}catch(Exception e) {
		e.printStackTrace();
		return false;
		
	 }
	}
	/**
	 * 별점을 별 문자열로 변환하는 유틸리티 메서드
	 * @param rating
	 * @return
	 */
	public String convertRatingToStars(int rating) {
		StringBuilder stars=new StringBuilder();
		for(int i=1; i<=5; i++) {
		  if (i <= rating) {
        stars.append("⭐");
				
			}else {
        stars.append("☆");
    }
		}
		
		return stars.toString();
	}
	
	public String convertAvgRatingToStars(double avgRating) {
		
		StringBuilder stars=new StringBuilder();
		int fullStars=(int) avgRating;
		double decimal=avgRating - fullStars;
		
	   for (int i = 1; i <= 5; i++) {
       if (i <= fullStars) {
           stars.append("⭐");
       } else if (i == fullStars + 1 && decimal >= 0.5) {
           stars.append("⭐");
       } else {
           stars.append("☆");
       }
   }
   return stars.toString();
 }
}