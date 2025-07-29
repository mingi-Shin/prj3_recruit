package kr.co.sist.user.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.sist.jwt.CustomUser;
import kr.co.sist.user.dto.ReviewDTO;
import kr.co.sist.user.dto.ReviewSearchDTO;
import kr.co.sist.user.mapper.ReviewMapper;
import kr.co.sist.user.service.ReviewService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService rs;
	private final ReviewMapper reviewMapper;
	
	//리뷰조회페이지 (페이징 추가)
	@GetMapping("/user/job_posting/review")
	public String review(@RequestParam(value = "corpNo", required = false) Long corpNo, 
	                    @RequestParam(value = "page", defaultValue = "1") int page,
	                    Model model) {
	    
	    // corpNo 유효성 검사를 먼저 수행
	    if (corpNo == null || corpNo <= 0) {
	        model.addAttribute("errorMessage", "올바른 기업 정보가 필요합니다.");
	        model.addAttribute("reviewList", new ArrayList<>());
	        ReviewDTO emptyStats = new ReviewDTO();
	        emptyStats.setAvgRating(0.0);
	        emptyStats.setTotalReviews(0);
	        model.addAttribute("reviewStats", emptyStats);
	        model.addAttribute("companyName", "알 수 없는 기업");
	        model.addAttribute("corpNo", corpNo);
	        return "user/job_posting/review";
	    }
	    
	    // 기본값 설정
	    List<ReviewDTO> reviewList = new ArrayList<>();
	    ReviewDTO reviewStats = new ReviewDTO();
	    reviewStats.setAvgRating(0.0);
	    reviewStats.setTotalReviews(0);
	    String companyName = "알 수 없는 기업";
	    
	    // 페이징 정보 초기화
	    ReviewSearchDTO searchDTO = new ReviewSearchDTO();
	    searchDTO.setCorpNo(corpNo);
	    searchDTO.setPage(page);
	    
	    // 디버깅용 로그 추가
	    System.out.println("===== 페이징 디버깅 =====");
	    System.out.println("corpNo: " + corpNo);
	    System.out.println("page: " + page);
	    System.out.println("offset: " + searchDTO.getOffset());
	    
	    try {
	        // 먼저 기업명 확인 (기업이 존재하는지 확인)
	        String name = rs.getCompanyName(corpNo);
	        if (name == null || name.trim().isEmpty()) {
	            model.addAttribute("errorMessage", "존재하지 않는 기업입니다.");
	        } else {
	            companyName = name;
	            
	            // 기업이 존재하면 페이징된 리뷰 데이터 조회
	            try {
	                reviewList = rs.getReviewsWithPaging(searchDTO);
	                if (reviewList == null) {
	                    reviewList = new ArrayList<>();
	                }
	            } catch (Exception e) {
	                System.out.println("리뷰 목록 조회 오류: " + e.getMessage());
	                e.printStackTrace();
	            }
	            
	            try {
	                ReviewDTO rating = rs.getRating(corpNo);
	                if (rating != null) {
	                    reviewStats = rating;
	                }
	            } catch (Exception e) {
	                System.out.println("리뷰 통계 조회 오류: " + e.getMessage());
	                e.printStackTrace();
	            }
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("errorMessage", "리뷰를 불러오는 중 오류가 발생했습니다.");
	    }
	    
	    // 모든 속성을 설정
	    model.addAttribute("reviewList", reviewList);
	    model.addAttribute("reviewStats", reviewStats);
	    model.addAttribute("companyName", companyName);
	    model.addAttribute("corpNo", corpNo);
	    
	    // 페이징 정보 추가 (1-based 그대로 전달)
	    model.addAttribute("currentPage", searchDTO.getPage()); 
	    model.addAttribute("totalPages", searchDTO.getTotalPages());
	    model.addAttribute("startPage", searchDTO.getStartPage()); 
	    model.addAttribute("endPage", searchDTO.getEndPage()); 
	    model.addAttribute("totalElements", searchDTO.getTotalElements());
	    
	    // 디버깅 정보 출력
	    System.out.println("=== 페이징 정보 ===");
	    System.out.println("현재 페이지: " + searchDTO.getPage());
	    System.out.println("전체 페이지: " + searchDTO.getTotalPages());
	    System.out.println("리뷰 개수: " + reviewList.size());
	    System.out.println("전체 리뷰 수: " + searchDTO.getTotalElements());
	    
	    return "user/job_posting/review";
	}

	@GetMapping("/user/job_posting/review_write") 
	public String reviewWritePage(@RequestParam("corpNo") Long corpNo,
	                              @AuthenticationPrincipal CustomUser userInfo,
	                              RedirectAttributes redirectAttributes,
	                              Model model) {
	    
	    // corpNo 유효성 검사
	    if (corpNo == null || corpNo <= 0) {
	        redirectAttributes.addFlashAttribute("errorMessage", "올바른 기업 정보가 필요합니다.");
	        return "redirect:/user/job_posting/review";
	    }
	    
	    // 로그인 상태 확인
	    if (userInfo == null) {
	        redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
	        return "redirect:/login";
	    }
	    
	    String email = userInfo.getEmail();
	    if (email == null || email.trim().isEmpty()) {
	        redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
	        return "redirect:/login";
	    }

	    // 기업 존재 여부 확인
	    String companyName = rs.getCompanyName(corpNo);
	    if (companyName == null || companyName.trim().isEmpty()) {
	        redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 기업입니다.");
	        return "redirect:/user/job_posting/review?corpNo=" + corpNo;
	    }

	    // 최종 합격 여부 확인
	    boolean eligible = rs.checkReviewEligibility(email, corpNo);
	    if (!eligible) {
	        redirectAttributes.addFlashAttribute("errorMessage", "해당 기업에 최종 합격한 경우에만 후기 작성이 가능합니다.");
	        return "redirect:/user/job_posting/review?corpNo=" + corpNo;
	    }

	    // 이미 작성했는지 확인
	    if (rs.hasUserReviewed(email, corpNo)) {
	        redirectAttributes.addFlashAttribute("errorMessage", "이미 작성한 리뷰가 있습니다.");
	        return "redirect:/user/job_posting/review?corpNo=" + corpNo;
	    }

	    model.addAttribute("corpNo", corpNo);
	    model.addAttribute("companyName", companyName);
	    return "user/job_posting/review_write";
	}

	@PostMapping("/user/job_posting/review")  // URL 경로 수정
	public String submitReview(@ModelAttribute ReviewDTO reviewDTO,
	                           @AuthenticationPrincipal CustomUser userInfo,
	                           RedirectAttributes redirectAttributes) {
	    
	    // 로그인 상태 확인
	    if (userInfo == null) {
	        redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
	        return "redirect:/login";
	    }
	    
	    String email = userInfo.getEmail();
	    if (email == null || email.trim().isEmpty()) {
	        redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
	        return "redirect:/login";
	    }

	    Long corpNo = reviewDTO.getCorpNo();
	    if (corpNo == null || corpNo <= 0) {
	        redirectAttributes.addFlashAttribute("errorMessage", "올바른 기업 정보가 필요합니다.");
	        return "redirect:/user/job_posting/review";
	    }

	    // 기업 존재 여부 확인
	    String companyName = rs.getCompanyName(corpNo);
	    if (companyName == null || companyName.trim().isEmpty()) {
	        redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 기업입니다.");
	        return "redirect:/user/job_posting/review?corpNo=" + corpNo;
	    }

	    // 최종 합격 여부 확인
	    if (!rs.checkReviewEligibility(email, corpNo)) {
	        redirectAttributes.addFlashAttribute("errorMessage", "후기는 최종 합격자만 작성 가능합니다.");
	        return "redirect:/user/job_posting/review?corpNo=" + corpNo;
	    }

	    // 중복 체크
	    if (rs.hasUserReviewed(email, corpNo)) {
	        redirectAttributes.addFlashAttribute("errorMessage", "이미 리뷰를 작성하셨습니다.");
	        return "redirect:/user/job_posting/review?corpNo=" + corpNo;
	    }

	    // 입력값 검증
	    if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
	        redirectAttributes.addFlashAttribute("errorMessage", "별점은 1~5점 사이여야 합니다.");
	        return "redirect:/user/job_posting/review_write?corpNo=" + corpNo;
	    }
	    
	    if (reviewDTO.getSummary() == null || reviewDTO.getSummary().trim().isEmpty()) {
	        redirectAttributes.addFlashAttribute("errorMessage", "한줄평을 입력해주세요.");
	        return "redirect:/user/job_posting/review_write?corpNo=" + corpNo;
	    }
	    
	    if (reviewDTO.getPros() == null || reviewDTO.getPros().trim().isEmpty()) {
	        redirectAttributes.addFlashAttribute("errorMessage", "장점을 입력해주세요.");
	        return "redirect:/user/job_posting/review_write?corpNo=" + corpNo;
	    }
	    
	    if (reviewDTO.getCons() == null || reviewDTO.getCons().trim().isEmpty()) {
	        redirectAttributes.addFlashAttribute("errorMessage", "단점을 입력해주세요.");
	        return "redirect:/user/job_posting/review_write?corpNo=" + corpNo;
	    }

	    try {
	        reviewDTO.setEmail(email);
	        reviewDTO.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

	        boolean result = rs.insertReview(reviewDTO);
	        if (result) {
	            redirectAttributes.addFlashAttribute("successMessage", "리뷰가 등록되었습니다.");
	        } else {
	            redirectAttributes.addFlashAttribute("errorMessage", "리뷰 등록 중 오류가 발생했습니다.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("errorMessage", "리뷰 등록 중 오류가 발생했습니다.");
	    }

	    return "redirect:/user/job_posting/review?corpNo=" + corpNo;
	}
	
}