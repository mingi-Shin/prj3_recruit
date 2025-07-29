package kr.co.sist.admin.review;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminReviewController {
	
private final AdminReviewService adminReviewService;
	
	public AdminReviewController(AdminReviewService adminReviewService) {
		this.adminReviewService = adminReviewService;
	}
	
	
	/**
   * 게시글 검색, 페이징, 정렬 API
   * @param dto 쿼리 파라미터가 DTO 필드로 자동 바인딩 됨
   * @return Map 형태로 게시글 리스트 + 페이징 정보 리턴
   */
	@GetMapping("/admin/admin_review")
	 public String getReviews(
       @RequestParam(defaultValue = "1") int currentPage,
       @RequestParam(defaultValue = "desc")String order,
       @RequestParam(defaultValue = "전체")String type,
       @RequestParam(defaultValue = "전체")String rating,
       @RequestParam(defaultValue = "")String keyword,
       Model model
) {
   // 총 글의 갯수
		Map<String, Object> map = new HashMap<String, Object>();
   // 페이징에 필요한 변수들
		map.put("type", type);
		map.put("rating", rating);
		map.put("keyword", keyword);
		int totalCount = adminReviewService.countSearch(map);
   int perPage = 10;  // 한 페이지당 보여질 글의 갯수
   int perBlock = 5;  // 현재 블럭에 보여질 페이지의 갯수
   int totalPage = (totalCount + perPage - 1) / perPage;  // 총 페이지 수
   int startPage = ((currentPage - 1) / perBlock) * perBlock + 1;  // 각 블럭에 보여질 시작 페이지
   int endPage = Math.min(startPage + perBlock - 1, totalPage);  // 각 블럭에 보여질 끝 페이지
   int start = (currentPage - 1) * perPage;  // DB에서 가져올 시작 번호
   int startNum = totalCount - ((currentPage - 1) * perPage);
	 map.put("start", start);
	 map.put("perPage", perPage);
	 map.put("order", order);
	 map.put("totalCount", totalCount);
	 
   // 목록 가져오기
   List<AdminReviewDTO> list = adminReviewService.getReviews(map);

   // Model에 필요한 데이터 저장
   model.addAttribute("type",type);
   model.addAttribute("keyword",keyword);
   model.addAttribute("rating",rating);
   model.addAttribute("order",order);
   model.addAttribute("totalCount", totalCount);
   model.addAttribute("reviewList", list);
   model.addAttribute("currentPage", currentPage);
   model.addAttribute("startPage", startPage);
   model.addAttribute("endPage", endPage);
   model.addAttribute("totalPage", totalPage);
   model.addAttribute("no", startNum);  // 현재 페이지의 첫 글 번호
   System.out.println("totalCount : " + totalCount);
   return "admin/admin_review";
}
	
	
	
	
	
	

}
