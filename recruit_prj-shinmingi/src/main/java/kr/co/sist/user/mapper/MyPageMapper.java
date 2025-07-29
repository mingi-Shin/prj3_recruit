package kr.co.sist.user.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.sist.user.dto.FavoriteCompanyDTO;
import kr.co.sist.user.dto.MyApplicantDTO;
import kr.co.sist.user.dto.MyApplicantSearchDTO;
import kr.co.sist.user.dto.MyPostingDTO;
import kr.co.sist.user.dto.MyReviewDTO;

@Mapper
public interface MyPageMapper {

	
	public List<MyApplicantDTO> selectMyAllApplicant(String email);
	public List<MyApplicantDTO> selectMyApplicant(MyApplicantSearchDTO searchDTO);
	public List<MyApplicantDTO> selectMyNextApplicant(MyApplicantSearchDTO searchDTO);
	
	public int updateApplicationCancel(int jobApplicationSeq);
	public int deleteApplication(int jobApplicationSeq);
	
	//스크랩한 공고
	public List<MyPostingDTO> selectMyScrapPosting(String email);
	public List<MyPostingDTO> selectMyNextScrapPosting(Map<String, Object> map);
	public int cntMyScrapPosting(String email);
	
	//최근 본 공고
	public List<MyPostingDTO> selectMyRecentPosting(String email);
	public List<MyPostingDTO> selectMyNextRecentPosting(Map<String, Object> map);
	public int cntMyRecentPosting(String email);
	
	//관심 기업
	public List<FavoriteCompanyDTO> selectMyFavoriteCompany(String email);
	public List<FavoriteCompanyDTO> selectMyNextFavoriteCompany(Map<String, Object> map);
	public int cntMyFavoriteCompany(String email);
	
	//기업 리뷰
	public List<MyReviewDTO> selectMyReview(String email);
	public int cntMyReview(String email);
	public void deleteMyReview(int reviewSeq);
	
}
