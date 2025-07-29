package kr.co.sist.user.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.sist.user.dto.ApplicantStatisticsDTO;
import kr.co.sist.user.dto.FavoriteCompanyDTO;
import kr.co.sist.user.dto.MyApplicantDTO;
import kr.co.sist.user.dto.MyApplicantSearchDTO;
import kr.co.sist.user.dto.MyPostingDTO;
import kr.co.sist.user.dto.MyReviewDTO;
import kr.co.sist.user.entity.UserEntity;
import kr.co.sist.user.mapper.MessageMapper;
import kr.co.sist.user.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {

	private final MyPageMapper mpMapper;
	private final MessageMapper messageMapper;

	// 내가 스크랩한 첫 공고 9개 가져오기
	public List<MyPostingDTO> searchMyScrapPosting(String email) {

		return mpMapper.selectMyScrapPosting(email);
	}

	// 내가 스크랩한 다음 공고 9개 가져오기
	public List<MyPostingDTO> searchMyNextScrapPosting(String email, int currentPage) {

		Map<String, Object> map = new HashMap<>();
		map.put("email", email);
		map.put("offset", (currentPage - 1) * 9);

		return mpMapper.selectMyNextScrapPosting(map);
	}

	// 내가 스크랩한 공고 개수
	public int cntMyScrapPosting(String email) {

		return mpMapper.cntMyScrapPosting(email);
	}
	
	// 내가 최근 본 첫 공고 9개 가져오기
	public List<MyPostingDTO> searchMyRecentPosting(String email) {
		
		return mpMapper.selectMyRecentPosting(email);
	}
	
	// 내가 최근 본 다음 공고 9개 가져오기
	public List<MyPostingDTO> searchMyNextRecentPosting(String email, int currentPage) {
		
		Map<String, Object> map = new HashMap<>();
		map.put("email", email);
		map.put("offset", (currentPage - 1) * 9);
		
		return mpMapper.selectMyNextRecentPosting(map);
	}
	
	// 내가 최근 본 공고 개수
	public int cntMyRecentPosting(String email) {
		
		return mpMapper.cntMyRecentPosting(email);
	}
	
	// 내 관심기업 9개 가져오기
	public List<FavoriteCompanyDTO> searchMyFavoriteCompany(String email) {
		
		return mpMapper.selectMyFavoriteCompany(email);
	}
	
	// 내 관심기업 다음 9개 가져오기
	public List<FavoriteCompanyDTO> searchMyNextFavoriteCompany(String email, int currentPage) {
		
		Map<String, Object> map = new HashMap<>();
		map.put("email", email);
		map.put("offset", (currentPage - 1) * 9);
		
		return mpMapper.selectMyNextFavoriteCompany(map);
	}
	
	// 내 관심기업 개수
	public int cntMyFavoriteCompany(String email) {
		
		return mpMapper.cntMyFavoriteCompany(email);
	}
	

	// 내 모든 지원 목록
	public List<MyApplicantDTO> searchMyAllApplicant(String email) {

		return mpMapper.selectMyAllApplicant(email);

	}

	// 내 지원 목록
	public List<MyApplicantDTO> searchMyApplicant(MyApplicantSearchDTO searchDTO) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		List<MyApplicantDTO> applicants = mpMapper.selectMyApplicant(searchDTO);
		for (MyApplicantDTO applicant : applicants) {

			// 디데이 추가
			LocalDate endDate = LocalDate.parse(applicant.getPostingEndDt(), formatter);
			long days = ChronoUnit.DAYS.between(LocalDate.now(), endDate);

			String dday;
			if (days == 0) {
				dday = "D-day";
			} else if (days > 0) {
				dday = "D-" + days;
			} else {
				dday = "마감";
			}
			applicant.setDDay(dday);

			// 날짜 가공
			applicant.setApplicationDate(applicant.getApplicationDate().substring(0, 10));

		}

		return applicants;
	}// searchMyApplicant
	
	//내 다음 지원 목록
	public List<MyApplicantDTO> searchMyNextApplicant(MyApplicantSearchDTO searchDTO) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		List<MyApplicantDTO> applicants = mpMapper.selectMyNextApplicant(searchDTO);
		for (MyApplicantDTO applicant : applicants) {

			// 디데이 추가
			LocalDate endDate = LocalDate.parse(applicant.getPostingEndDt(), formatter);
			long days = ChronoUnit.DAYS.between(LocalDate.now(), endDate);

			String dday;
			if (days == 0) {
				dday = "D-day";
			} else if (days > 0) {
				dday = "D-" + days;
			} else {
				dday = "마감";
			}
			applicant.setDDay(dday);

			// 날짜 가공
			applicant.setApplicationDate(applicant.getApplicationDate().substring(0, 10));

		}

		return applicants;
	}// searchMyNextApplicant

	// 지원 취소
	public int cancelApplication(int jobApplicatioinSeq) {

		return mpMapper.updateApplicationCancel(jobApplicatioinSeq);

	}

	// 지원 내역 삭제
	public int removeApplication(int jobApplicationSeq) {

		return mpMapper.deleteApplication(jobApplicationSeq);
	}

	// 지원자 통계 가공
	public ApplicantStatisticsDTO getApplicantStatistics(List<MyApplicantDTO> applicantDTO) {

		ApplicantStatisticsDTO statistics = new ApplicantStatisticsDTO();

		// 모든 내 지원 내역을 돌며 각각의 상태 수 설정
		for (MyApplicantDTO dto : applicantDTO) {

			switch (dto.getPassStage()) {
			case 1:
				statistics.setDocPassed(statistics.getDocPassed() + 1);
				break;
			case 2:
				statistics.setPassed(statistics.getPassed() + 1);
				break;
			case 3:
				statistics.setFailed(statistics.getFailed() + 1);
				break;
			}

			statistics.setCompleted(statistics.getCompleted() + 1);
		}
		return statistics;

	}// getApplicantStatistics
	
	//회원 탈퇴 처리
	@Transactional
	public void resignMember(UserEntity userEntity) {
		
		//활동 상태 탈퇴로 변경
		userEntity.setActiveStatus(2);
	}
	
	//내 기업 후기
	public List<MyReviewDTO> searchMyReview(String email){
		
		return mpMapper.selectMyReview(email);
	}
	
	//내 후기 개수
	public int cntMyReview(String email) {
		return mpMapper.cntMyReview(email);
	}
	
	//내 후기 삭제
	public void removeMyReview(int reviewSeq) {
		mpMapper.deleteMyReview(reviewSeq);
	}

}// class
