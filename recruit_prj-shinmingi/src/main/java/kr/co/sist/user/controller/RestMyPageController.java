package kr.co.sist.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.sist.jwt.CustomUser;
import kr.co.sist.login.UserRepository;
import kr.co.sist.user.dto.FavoriteCompanyDTO;
import kr.co.sist.user.dto.MessageDTO;
import kr.co.sist.user.dto.MessageSearchDTO;
import kr.co.sist.user.dto.MyApplicantDTO;
import kr.co.sist.user.dto.MyApplicantSearchDTO;
import kr.co.sist.user.dto.MyPostingDTO;
import kr.co.sist.user.entity.UserEntity;
import kr.co.sist.user.service.MessageService;
import kr.co.sist.user.service.MyPageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RestMyPageController {

	private final MyPageService myPageServ;
	private final MessageService messageServ;

	private final UserRepository userRepos;

	// 필터링해서 이후 지원내역 9개 가져오기
	@GetMapping("/mypage/apply")
	public Map<String, Object> getApplyList(@AuthenticationPrincipal CustomUser userInfo, @RequestBody MyApplicantSearchDTO searchDTO) {
		
		Map<String, Object> result = new HashMap<>();

		UserEntity userEntity = userRepos.findById(userInfo.getEmail()).orElse(null);
		searchDTO.setEmail(userEntity.getEmail());
		searchDTO.setOffset((searchDTO.getCurrentPage() - 1) * 9);
		
		List<MyApplicantDTO> applicants = myPageServ.searchMyNextApplicant(searchDTO);
		
		result.put("result", "success");
		result.put("applicants", applicants);

		return result;
		
	}

	// 지원한 공고 지원 취소
	@PutMapping("/mypage/application/{jobApplicationSeq}")
	public String cancelApplication(@PathVariable int jobApplicationSeq) {

		int result = myPageServ.cancelApplication(jobApplicationSeq);

		if (result >= 1) {
			return "success";
		}

		return "fail";

	}// deleteApplication

	// 지원취소한 지원내역 삭제
	@DeleteMapping("/mypage/application/{jobApplicationSeq}")
	public void deleteApplication(@PathVariable int jobApplicationSeq) {

		myPageServ.removeApplication(jobApplicationSeq);

	}

	// 검색 & 페이징 된 메일 목록 가져오기
	@GetMapping("/mypage/messages")
	public Map<String, Object> returnMessage(@AuthenticationPrincipal CustomUser userInfo,
			@RequestBody MessageSearchDTO searchDTO) {

		Map<String, Object> result = new HashMap<>();

		UserEntity userEntity = userRepos.findById(userInfo.getEmail()).orElse(null);

		// 모든 메일 개수 가져오기
		int totalCnt = messageServ.cntMyAllMesage(userEntity.getEmail());

		// 검색조건에 유저 설
		searchDTO.setEmail(userEntity.getEmail());

		// 검색된 메일 개수 가져오기
		int searchCnt = messageServ.cntMyMessage(searchDTO);

		// 페이징 된 메일 목록 가져오기
		searchDTO.setTotalCnt(totalCnt);
		searchDTO.setSearchCnt(searchCnt);
		List<MessageDTO> messages = messageServ.searchMyMessage(searchDTO);

		result.put("result", "success");
		result.put("data", messages);

		return result;
	}

	// 메일 읽음
	@PutMapping("/mypage/message/{messageSeq}")
	public void readMessage(@PathVariable int messageSeq) {

		// 메일 읽음 처리
		messageServ.toggleReadMessage(messageSeq);

	}// readMessage

	// 체크된 메일들 읽음 여두 토글처리
	@PutMapping("/mypage/messages/{selectedSeq}")
	public void readMessages(@PathVariable List<Integer> selectedSeq) {

		for (int messageSeq : selectedSeq) {
			messageServ.toggleReadMessage(messageSeq);
		}
	}// readMessages

	// 체크된 메일들 삭제
	@DeleteMapping("/mypage/messages/{selectedSeq}")
	public void removeMessages(@PathVariable List<Integer> selectedSeq) {

		for (int messageSeq : selectedSeq) {
			messageServ.removeMessage(messageSeq);
		}
	}// deleteMessages

	// 스크랩한 공고 페이징 해서 가져오기
	@GetMapping("/mypage/scrap/{currentPage}")
	public List<MyPostingDTO> getScrapPosting(@PathVariable int currentPage,
			@AuthenticationPrincipal CustomUser userInfo) {

		List<MyPostingDTO> posting = myPageServ.searchMyNextScrapPosting(userInfo.getEmail(), currentPage);
		return posting;
	}

	// 최근 본 공고 페이징 해서 가져오기
	@GetMapping("/mypage/scrap/resent/{currentPage}")
	public List<MyPostingDTO> getRecentPosting(@PathVariable int currentPage,
			@AuthenticationPrincipal CustomUser userInfo) {

		List<MyPostingDTO> posting = myPageServ.searchMyNextRecentPosting(userInfo.getEmail(), currentPage);
		return posting;
	}

	// 관심 기업 페이징 해서 가져오기
	@GetMapping("/mypage/company/{currentPage}")
	public List<FavoriteCompanyDTO> getFavoriteCompany(@PathVariable int currentPage,
			@AuthenticationPrincipal CustomUser userInfo) {

		List<FavoriteCompanyDTO> companies = myPageServ.searchMyNextFavoriteCompany(userInfo.getEmail(), currentPage);
		return companies;
	}

	// 유저 탈퇴
	@DeleteMapping("/mypage/member")
	public String resignMember(@AuthenticationPrincipal CustomUser userInfo) {

		UserEntity userEntity = userRepos.findById(userInfo.getEmail()).orElse(null);

		try {
			myPageServ.resignMember(userEntity);
			return "success";

		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}
	
	

}// class
