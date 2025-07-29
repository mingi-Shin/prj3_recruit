package kr.co.sist.user.controller;

import org.apache.xmlbeans.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

import kr.co.sist.jwt.CustomUser;
import kr.co.sist.user.dto.InquiryRequestDTO;
import kr.co.sist.user.dto.InquiryResponseDTO;
import kr.co.sist.user.service.InquiryService;

@Controller
@RequestMapping("/user/help")
public class InquiryController {

    @Autowired
    private InquiryService helpService;

    // 고객센터 메인 페이지
    @GetMapping("/help")
    public String helpCenter(Model model) {
        // 카테고리별 문의 통계 추가
        Map<String, Long> categoryStats = helpService.getInquiryStatsByCategory();
        model.addAttribute("categoryStats", categoryStats);
        return "user/help/help";
    }

    // 카테고리별 FAQ 목록 API
    @GetMapping("/api/faqs/{category}")
    @ResponseBody
    public ResponseEntity<List<InquiryResponseDTO>> getFAQsByCategory(@PathVariable String category) {
        try {
            List<InquiryResponseDTO> faqs = helpService.getFAQsByCategory(category);
            return ResponseEntity.ok(faqs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 사용자 유형별 FAQ 목록 API
    @GetMapping("/api/inquiry-faqs")
    @ResponseBody
    public ResponseEntity<Map<String, List<InquiryResponseDTO>>> getAllFAQs(
            @RequestParam(defaultValue = "user") String userType) {
        try {
            Map<String, List<InquiryResponseDTO>> faqsByCategory = helpService.getFAQsByUserType(userType);
            return ResponseEntity.ok(faqsByCategory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 카테고리별 문의 통계 API
    @GetMapping("/api/stats/category")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getCategoryStats() {
        try {
            Map<String, Long> stats = helpService.getInquiryStatsByCategory();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/test/setSession")
    @ResponseBody
    public String setTestSession(HttpSession session) {
        session.setAttribute("userEmail", "testuser@example.com");
        return "세션에 임시 이메일과 adminId가 설정되었습니다.";
    }

    // 문의 등록 처리
    @PostMapping("/inquiry")
    @ResponseBody
    public ResponseEntity<?> submitInquiry(
    		@RequestParam("userType") String userType,
            @RequestParam("inquiryType") String category,
            @RequestParam("inquiryTitle") String title,
            @RequestParam("inquiryContent") String content,
            @RequestParam(value = "attachFile", required = false) MultipartFile file,
            @AuthenticationPrincipal CustomUser user) {

        try {
            if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "로그인이 필요합니다."));
            }

            String userEmail = user.getEmail().trim();

            // CustomUser에서 userType 가져오기 (예시)

            InquiryRequestDTO inquiryRequest = new InquiryRequestDTO();
            inquiryRequest.setEmail(userEmail);
            inquiryRequest.setTitle(title != null ? title.trim() : "");
            inquiryRequest.setContent(content != null ? content.trim() : "");
            inquiryRequest.setCategory(category != null ? category.trim() : "");
            inquiryRequest.setAnswerStat("N");
            inquiryRequest.setUserType(userType); // 여기에 userType 세팅


            helpService.saveInquiry(inquiryRequest);

            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "문의가 성공적으로 접수되었습니다."));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "문의 접수 중 오류 발생: " + e.getMessage()));
        }
    }

 // 카테고리별 문의 목록 조회 페이지
    @GetMapping("/list/{category}")
    public String getInquiriesByCategory(@PathVariable String category, 
                                         @RequestParam(defaultValue = "0") int page,
                                         Model model) {
        int pageSize = 10; // 한 페이지에 10개씩
        Page<InquiryResponseDTO> inquiryPage = helpService.getInquiriesByCategory(category, PageRequest.of(page, pageSize));

        model.addAttribute("inquiries", inquiryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", inquiryPage.getTotalPages());
        model.addAttribute("category", category);

        return "user/help/inquiry_list"; // Thymeleaf 템플릿 경로
    }

}