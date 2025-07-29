package kr.co.sist.admin.review;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.sist.user.mapper.ReviewMapper;

@Service
public class AdminReviewService {
	
	private final ReviewMapper reviewMapper;
	
	public AdminReviewService(ReviewMapper reviewMapper) {
		this.reviewMapper = reviewMapper;
	}
	
	public List<AdminReviewDTO> getReviews(Map<String, Object> map){
	
		return reviewMapper.selectSearchReviews(map);
	}
	
	public int countSearch(Map<String, Object> map) {
		
		return reviewMapper.countSearch(map);
	}
	
	public boolean deleteReview(Map<String,Object> map) {
		int count = reviewMapper.deleteReview(map);
		return count > 0;
	}
}