package kr.co.sist.user.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import kr.co.sist.jwt.CustomUser;
import kr.co.sist.login.UserRepository;
import kr.co.sist.user.dto.ApplicantStatisticsDTO;
import kr.co.sist.user.dto.FavoriteCompanyDTO;
import kr.co.sist.user.dto.MessageDTO;
import kr.co.sist.user.dto.MessageSearchDTO;
import kr.co.sist.user.dto.MessageStatisticsDTO;
import kr.co.sist.user.dto.MyApplicantDTO;
import kr.co.sist.user.dto.MyApplicantSearchDTO;
import kr.co.sist.user.dto.MyPostingDTO;
import kr.co.sist.user.dto.MyReviewDTO;
import kr.co.sist.user.entity.UserEntity;
import kr.co.sist.user.service.MessageService;
import kr.co.sist.user.service.MyPageService;
import kr.co.sist.util.CipherUtil;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MyPageController {

	private final MyPageService myPageServ;
	private final MessageService messageServ;

	private final UserRepository userRepos;

	private final CipherUtil cu;

	@GetMapping("/user/mypage")
	public String myPageMain(@AuthenticationPrincipal CustomUser userInfo, Model model) {

		UserEntity userEntity = userRepos.findById(userInfo.getEmail()).orElse(null);
		userEntity.setPhone(cu.decryptText(userEntity.getPhone()));

		// 전체 지원목록 가져오기
		List<MyApplicantDTO> applicantDTO = myPageServ.searchMyAllApplicant(userEntity.getEmail());

		// 지원 통계 가져오기
		ApplicantStatisticsDTO statistics = myPageServ.getApplicantStatistics(applicantDTO);

		model.addAttribute("statistics", statistics);
		model.addAttribute("user", userEntity);

		return "user/mypage/mypage";

	}

	//탈퇴 페이지로 이동
	@GetMapping("/user/mypage/account_resign")
	public String accountResign(@AuthenticationPrincipal CustomUser userInfo, Model model) {
		
		return "user/mypage/account_resign";
	}

	// 지원 목록으로 이동
	@GetMapping("/user/mypage/apply_list")
	public String applyList(@ModelAttribute MyApplicantSearchDTO searchDTO, @AuthenticationPrincipal CustomUser userInfo, Model model) {

		UserEntity userEntity = userRepos.findById(userInfo.getEmail()).orElse(null);

		searchDTO.setEmail(userEntity.getEmail());
		
		//페이징된 지원 목록 가져오기
		List<MyApplicantDTO> applicantDTO = myPageServ.searchMyApplicant(searchDTO);

		//전체 지원 목록 가져오기
		List<MyApplicantDTO> allApplicantDTO = myPageServ.searchMyAllApplicant(userEntity.getEmail());
		// 지원 통계 가져오기
		ApplicantStatisticsDTO statistics = myPageServ.getApplicantStatistics(allApplicantDTO);
		
		model.addAttribute("statistics", statistics);
		model.addAttribute("applicants", applicantDTO);
		model.addAttribute("search", searchDTO);

		return "user/mypage/apply_list";
	}

	// 메일 목록으로 이동
	@GetMapping("/user/mypage/mail_list")
	public String mailList(@AuthenticationPrincipal CustomUser userInfo, @ModelAttribute MessageSearchDTO searchDTO,
			Model model) {

		UserEntity userEntity = userRepos.findById(userInfo.getEmail()).orElse(null);

		// 모든 메일 개수 가져오기
		int totalCnt = messageServ.cntMyAllMesage(userEntity.getEmail());

		// 검색 조건 및 페이징 DTO
		searchDTO.setEmail(userEntity.getEmail());
		searchDTO.setTotalCnt(totalCnt);

		// 검색된 메일 개수 가져오기
		int searchCnt = messageServ.cntMyMessage(searchDTO);
		searchDTO.setSearchCnt(searchCnt);
		
		// 페이징된 메일목록 가져오기
		List<MessageDTO> messages = messageServ.searchMyMessage(searchDTO);

		// 모든 메일로 통계 집계
		List<MessageDTO> allMessages = messageServ.searchMyAllMessage(userEntity.getEmail());
		MessageStatisticsDTO statistics = messageServ.getMessageStatistics(allMessages);

		model.addAttribute("messages", messages);
		model.addAttribute("statistics", statistics);
		model.addAttribute("search", searchDTO);

		return "user/mypage/mail_list";
	}

	//내 기업 후기 페이지
	@GetMapping("/user/mypage/company_reviews")
	public String companyReviews(@AuthenticationPrincipal CustomUser userInfo, Model model) {
		
		List<MyReviewDTO> reviews = myPageServ.searchMyReview(userInfo.getEmail());
		int totalCnt = myPageServ.cntMyReview(userInfo.getEmail());
		
		System.out.println("------------------------------------" + totalCnt);
		
		model.addAttribute("reviews", reviews);
		model.addAttribute("totalCnt", totalCnt);
		
		return "user/mypage/company_reviews";
	}
	
	//기업후기 삭제
	@PostMapping("/mypage/reviewDelete")
	public String deleteReview(int reviewSeq) {
		myPageServ.removeMyReview(reviewSeq);
		
		return "redirect:/user/mypage/company_reviews";
	}

	//관심기업 페이지로 이동
	@GetMapping("/user/mypage/favorite_companies")
	public String favoriteCompanies(@AuthenticationPrincipal CustomUser userInfo, Model model) {
		
		List<FavoriteCompanyDTO> companies = myPageServ.searchMyFavoriteCompany(userInfo.getEmail());
		int totalCnt = myPageServ.cntMyFavoriteCompany(userInfo.getEmail());
		
		model.addAttribute("companies",companies);
		model.addAttribute("totalCnt", totalCnt);
		
		return "user/mypage/favorite_companies";
	}

	//최근 본 공고 페이지로 이동
	@GetMapping("/user/mypage/recent_posting")
	public String recentPosting(@AuthenticationPrincipal CustomUser userInfo, Model model) {
		
		List<MyPostingDTO> postings = myPageServ.searchMyRecentPosting(userInfo.getEmail());
		int totalCnt = myPageServ.cntMyRecentPosting(userInfo.getEmail());
		
		model.addAttribute("postings",postings);
		model.addAttribute("totalCnt", totalCnt);
		
		return "user/mypage/recent_posting";
	}

	//스크랩한 공고 페이지로 이동
	@GetMapping("/user/mypage/scrap_posting")
	public String scrapPosting(@AuthenticationPrincipal CustomUser userInfo, Model model) {

		
		List<MyPostingDTO> postings = myPageServ.searchMyScrapPosting(userInfo.getEmail());
		int totalCnt = myPageServ.cntMyScrapPosting(userInfo.getEmail());
		
		model.addAttribute("postings",postings);
		model.addAttribute("totalCnt", totalCnt);
		
		return "user/mypage/scrap_posting";
	}
}
