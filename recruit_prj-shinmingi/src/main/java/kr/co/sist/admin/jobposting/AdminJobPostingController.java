package kr.co.sist.admin.jobposting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.sist.corp.dto.JobPostingDTO;
import kr.co.sist.jwt.CustomUser;
import kr.co.sist.login.UserRepository;
import kr.co.sist.user.controller.JobPostingController;
import kr.co.sist.user.dto.JobPostDTO;
import kr.co.sist.user.dto.ResumeDTO;
import kr.co.sist.user.dto.UserDTO;
import kr.co.sist.user.entity.UserEntity;
import kr.co.sist.user.service.JobPostingService;
import kr.co.sist.user.service.RecentViewService;
import kr.co.sist.user.service.ResumeService;
import kr.co.sist.user.service.UserNoticeService;
import kr.co.sist.util.CipherUtil;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor 
public class AdminJobPostingController {
	
	private final AdminJobPostingService adminJobPostingService;
	private final JobPostingController jobPostingController;
	 private final JobPostingService jps;
   private final UserRepository ur;
   private final CipherUtil cu;
   private final  ResumeService rs;
   private final RecentViewService recentViewService;
   private final UserNoticeService noticeService;
	
	@GetMapping("/admin/admin_jobPosting")
	public String jobPosting(
			@RequestParam(defaultValue = "1") int currentPage,
      @RequestParam(defaultValue = "desc")String order,
      @RequestParam(defaultValue = "전체")String type,
      @RequestParam(defaultValue = "전체")String exp_level,
      @RequestParam(defaultValue = "전체")String edu_level,
      @RequestParam(defaultValue = "전체")String employ_type,
      @RequestParam(defaultValue = "")String keyword,
			Model model) {
	// 총 글의 갯수
			Map<String, Object> map = new HashMap<String, Object>();
	   // 페이징에 필요한 변수들
			map.put("type", type);
			map.put("exp_level", exp_level);
			map.put("edu_level", edu_level);
			map.put("employ_type", employ_type);
			map.put("keyword", keyword);
			int totalCount = adminJobPostingService.countSearch(map);
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
		 List<JobPostingDTO> list = adminJobPostingService.getPostings(map);

	   // Model에 필요한 데이터 저장
	   model.addAttribute("type",type);
	   model.addAttribute("keyword",keyword);
	   model.addAttribute("exp_level", exp_level);
	   model.addAttribute("edu_level", edu_level);
	   model.addAttribute("employ_type", employ_type);
	   model.addAttribute("order",order);
	   model.addAttribute("totalCount", totalCount);
	   model.addAttribute("jobPostingList", list);
	   model.addAttribute("currentPage", currentPage);
	   model.addAttribute("startPage", startPage);
	   model.addAttribute("endPage", endPage);
	   model.addAttribute("totalPage", totalPage);
	   model.addAttribute("no", startNum);  // 현재 페이지의 첫 글 번호
		
		return "admin/admin_jobPosting";
	}
	
	@GetMapping("/admin/admin_job_posting_detail")
  public String JobPostingDetail(@RequestParam(required = false) Integer jobPostingSeq,
      @AuthenticationPrincipal CustomUser userInfo,
      Model model) {

	JobPostDTO jDto = jps.findById(jobPostingSeq);
	
	// 조회수 증가
	if (jDto != null) {
	jps.incrementViewCount(jobPostingSeq); // 조회수 증가
	}
	
	// 공고 데이터 모델에 추가
	model.addAttribute("jDto", jDto);
	
	if (userInfo != null) {
	UserEntity userEntity = ur.findById(userInfo.getEmail()).orElse(null);
	if (userEntity != null) {
	try {
	
	
	// 유저 정보 복호화
	userEntity.setPhone(cu.decryptText(userEntity.getPhone()));
	String birth = userEntity.getBirth();
	} catch (Exception e) {
	// 복호화 실패 시 처리  
	userEntity.setPhone("복호화 실패");
	userEntity.setBirth("복호화 실패");
	}
	// 복호화된 데이터를 UserDTO에 담아서 전달
	UserDTO user = new UserDTO(userEntity);
	model.addAttribute("user", user);
	
	recentViewService.saveRecentViewPosting(userInfo.getEmail(), jobPostingSeq);
	
	List<ResumeDTO> resumes = rs.searchAllResumeByUser(userInfo.getEmail());
	model.addAttribute("resumes", resumes);
	}
	}
	
	return "admin/admin_job_posting_detail";
	}
}
