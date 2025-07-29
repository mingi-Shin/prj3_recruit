package kr.co.sist.corp.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.sist.corp.dto.CorpDTO;
import kr.co.sist.corp.dto.JobPostingApplicantStatsDTO;
import kr.co.sist.corp.dto.JobPostingDTO;
import kr.co.sist.corp.service.JobPostingCorpService;
import kr.co.sist.globalController.Exceptions.NotFoundException;
import kr.co.sist.jwt.CustomUser;
import kr.co.sist.jwt.JWTUtil;
import kr.co.sist.user.dto.PositionCodeDTO;
import kr.co.sist.user.dto.TechStackDTO;
import kr.co.sist.user.dto.UserDTO;

@Controller
public class JobPostController {
	
	private final JWTUtil jwtUtil;
	private final JobPostingCorpService jpcService;
	
	public JobPostController(JWTUtil jwtUtil, JobPostingCorpService jpcService) {
	  this.jpcService = jpcService;
		this.jwtUtil = jwtUtil;
	}

  /**
   * 공고등록 form으로 가기 : 권한체크 
   */
  @GetMapping("/corp/jobPostingForm")
  public String getJobPostingForm(Model model, HttpServletRequest request, @AuthenticationPrincipal CustomUser user) {
  	
  	if(user == null) {
  		return "redirect:/accessDenied";
  	}
  	boolean hasCorpAuth = user.getAuthorities().stream()
  													.anyMatch(auth -> "ROLE_CORP".equals(auth.getAuthority()));
  	
  	if( !hasCorpAuth) {
  		return "redirect:/accessDenied";
  	}
  	
  	//db조작시 user활용  
  	//user.getEmail();
  	//user.getCorpNo();
  	
	 return "corp/jobPosting/jobPostingForm";
  }
  
  /**
   * 나의 공고 리스트 화면으로 가기 : 권한체크 
   */
  @GetMapping("/corp/myJobPostingListPage")
  public String getMyJobPostingListPage(@AuthenticationPrincipal CustomUser user, Model model) {
  	
  	//회원권한 체크
  	if(user == null) {
  		return "redirect:/accessDenied";
  	}
  	boolean hasCorpAuth = user.getAuthorities().stream()
  													.anyMatch(auth -> "ROLE_CORP".equals(auth.getAuthority()));
  	
  	if( !hasCorpAuth) {
  		return "redirect:/accessDenied";
  	}
  	
  	//공고 데이터 model에 갖고 보내기
  	JobPostingDTO jpDTO = new JobPostingDTO();
  	jpDTO.setCorpNo(user.getCorpNo());
  	jpDTO.setPostSts("ing");
  	jpDTO.setOrderBy("start");
  	
  	try {
  		List <Map<String, Integer>> postCntMapList =  jpcService.selectMyJobPostingCnt(jpDTO.getCorpNo());
  		List <JobPostingDTO> postList = jpcService.selectMyJobPosting(jpDTO);
  		
  		model.addAttribute("postCntMapList", postCntMapList);
  		model.addAttribute("postList", postList);
		} catch (Exception e) {
			e.printStackTrace();
		} 
  	return "corp/jobPosting/myJobPostingListPage";
  }
  
  
  /**
   * 새로운 공고 등록 (JSON)
   */
  @PostMapping("/corp/uploadJobPosting")
  public ResponseEntity<?> registerJobPost(@RequestBody JobPostingDTO jpDTO) {
  	
  	//form 2차 검증(if) 나중에 하자 시간없다 
  	
  	try {
  		//공고 등록 호출
  		jpcService.uploadJobPost(jpDTO);
  		return ResponseEntity.ok("공고 등록 성공");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
    
  }
  
  /**
   * 공고 수정하기
   */
  @PostMapping("/corp/updateJobPosting")
  public ResponseEntity<?> updateJobPost(@RequestBody JobPostingDTO jpDTO) {
  	
  	try {
  		jpcService.updateJobPost(jpDTO);
  		return ResponseEntity.ok("공고 수정 성공");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
  }
  
  //예외처리 어떻게 하지? https://chatgpt.com/s/t_686fe64224988191838976d9bfa9aea0
  /**
   * 컨트롤러에서 catch 안 하면, 예외는 스프링 기본 예외 처리기에 의해 처리됨
			→ 콘솔에 에러 로그만 출력되고, 브라우저에 500 Internal Server Error + 흰 화면 or HTML 에러 페이지 나올 수 있음
   */
  
  /**
   * 공고form 페이지에서 포지션 검색 비동기 수신 
   */
  @GetMapping("/corp/searchPositionByKeyword") //Get은 @Requestbody를 가질수 없음
  public ResponseEntity<?> searchPositionByKeyword(@RequestParam String keyword){
  	List<PositionCodeDTO> resultList = jpcService.pDTOList(keyword);
  	
  	return ResponseEntity.ok(resultList); // → JSON 배열로 자동 직렬화됨
  }
  
  /**
   * 공고form 페이지에서 기술스택 검색 비동기 수신 
   */
  @GetMapping("/corp/searchStackByKeyword")
  public ResponseEntity<?> searchTechStackBykeyword(@RequestParam String keyword){
  	List<TechStackDTO> resultList = jpcService.tsDTO(keyword);
  	
  	return ResponseEntity.ok(resultList);
  }
  
  /**
   * 나의 공고 리스트 페이지에 뿌릴 자료 호출 (비동기용!) RESTful 하게 ㄱㄱ 
   * 필요 변수: corpNO, postSts (total, ing, end), orderBy(start, end, viewCnt) 
   */
  @GetMapping("/corp/postings/selectby/{corpNo}/{postSts}/{orderBy}") //PostMapping 할땐 @RequestBody 받아야 오류 안남 
  public ResponseEntity<?> getMyAllPosting(@PathVariable("corpNo") long corpNo, @PathVariable("postSts") String postSts, @PathVariable("orderBy") String orderBy){
  	
  	System.out.println("fetch로 공고 가져오기 디버깅 : ");
  	System.out.println(corpNo + "/ " + postSts + "/ " + orderBy);
  	
  	JobPostingDTO jpDTO = new JobPostingDTO();
  	jpDTO.setCorpNo(corpNo);
  	jpDTO.setPostSts(postSts);
  	jpDTO.setOrderBy(orderBy);
  	
  	try {
  		List <Map<String, Integer>> postCntMapList =  jpcService.selectMyJobPostingCnt(jpDTO.getCorpNo());
  		List <JobPostingDTO> postList = jpcService.selectMyJobPosting(jpDTO);
  		
  		Map<String, Object> result = new HashMap<String, Object>();
  		result.put("postCnt", postCntMapList);
  		result.put("postList", postList);
  		
  		return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("공고 리스트 호출 실패: " + e.getMessage());
		} 
  	
  }
  
  /**
   * 공고 조기마감 (논리적 삭제) 
   */
  @DeleteMapping("/corp/postings/{jobPostingSeq}") 
  public ResponseEntity<?> updatePostingToFinish(@PathVariable("jobPostingSeq") int jobPostingSeq) {
  	System.out.println("디버깅: 컨트롤러 -> updatePostingToFinish 실행 ");
  	try {
			jpcService.updateJobPotingToEnd(jobPostingSeq);
			return ResponseEntity.ok("공고 조기마감 처리 성공");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("공고 조기마감 처리 실패 : " + e.getMessage());
		}
  }
  
  /**
   *	공고 수정 폼으로 이동 
   */
  @GetMapping("/corp/postings/updateForm/{jobPostingSeq}")
  public String getUpdateForm(@AuthenticationPrincipal CustomUser loginUser, @PathVariable("jobPostingSeq") int jobPostingSeq, Model model) {
  	//특정공고 가져와 
  	long corpNo = loginUser.getCorpNo();
  	JobPostingDTO jpDTO = jpcService.selectMyJobPostingOne(corpNo, jobPostingSeq);
  	
  	model.addAttribute("posting", jpDTO);
  	System.out.println(jpDTO);
  	
  	return "corp/jobPosting/updateJobPostingForm";
  }
  
  /**
   * 공고에 해당하는 지원자들 통계자료 가져오기 
   */
  @GetMapping("/corp/jobPosting/applicantStats/{jobPostingSeq}")
  public ResponseEntity<?> getApplicantStats(@PathVariable int jobPostingSeq){
  	System.out.println("[디버깅] controller - getApplicantStats 실행 : 매개변수 -> " +  jobPostingSeq);
  	
  	try {
  		
  		JobPostingApplicantStatsDTO jpasDTO = jpcService.selectApplicantStats(jobPostingSeq);
  		if(jpasDTO == null) {
  			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("통계 자료가 존재하지 않음 ");
  		}
  		
  		jpasDTO.setJobPostingSeq(jobPostingSeq);
  		return ResponseEntity.ok(jpasDTO);
  		
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("데이터 무결성 위반: 존재하지 않는 공고번호");
		} catch (Exception e){
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류 발생");
		}
  }
  
  
  /**
   *  공고의 지원자들 정보를 엑셀로 뽑기 
   */
  @GetMapping("/corp/applicantInfoList/excel/download/{jobPostingSeq}/{title}")
  public ResponseEntity<Resource> downloadApplicantInfoListExcel(@PathVariable("jobPostingSeq") int jobPostingSeq, @PathVariable("title") String title){
  	Resource result = jpcService.excelDownload(jobPostingSeq);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + title + "_지원자리스트.xlsx")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(result);
  }
  
  
}
/**
 * 자주 쓰는 형태 패턴 
// 200 OK + 데이터
return ResponseEntity.ok(data);

// 201 Created
return new ResponseEntity<>("생성 완료", HttpStatus.CREATED);

// 204 No Content
return ResponseEntity.noContent().build();

// 400 Bad Request
return ResponseEntity.badRequest().body("잘못된 요청");

// 404 Not Found
return ResponseEntity.status(HttpStatus.NOT_FOUND).body("찾을 수 없음");

// 500 Internal Server Error
return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
 */
