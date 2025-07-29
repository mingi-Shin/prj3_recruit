package kr.co.sist.login;
import kr.co.sist.corp.mapper.JobPostingCorpMapper;
import kr.co.sist.jwt.CustomUser;
import kr.co.sist.jwt.JWTUtil;
import kr.co.sist.user.dto.ResumeRequestDTO;
import kr.co.sist.user.dto.ResumeResponseDTO;
import kr.co.sist.user.dto.UserDTO;
import kr.co.sist.user.entity.UserEntity;
import kr.co.sist.user.service.AttachmentService;
import kr.co.sist.user.service.PositionCodeService;
import kr.co.sist.user.service.ResumeService;
import kr.co.sist.util.CipherUtil;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@RequiredArgsConstructor //-> 생성자주입방식 생성자 코드 대신 어노테이션
@Controller
public class LoginController {

    private final CipherUtil cipherUtil;

	private final LoginJoinService ljs; 
	private final JWTUtil jwtUtil;
	private final Environment env;
	
	private final JobPostingCorpMapper jpm;
	private final ResumeService rServ;
	private final AttachmentService aServ;
	private final PositionCodeService pcs;

	private final UserRepository userRepos;
	
	private final ObjectMapper objMapper;

  /**
   * 회원 로그인 페이지로 이동
   * @return
   */
  @GetMapping("/login")
  public String goCorpLoginForm() {
    return "login/login_form";
  }
    
  /**
   * 통합(일반, 기업) 로그인 처리 -> 현재 security의 핸들러에서 대체중 
   * @return
   */
/**
  //@PostMapping("/loginProcess")
  public String memberLoginProcess(String email, String password, HttpServletResponse response, RedirectAttributes rttr) {
  	
  	//1. 사용자 인증
  	UserDTO uDTO = ljs.authenticate(email, password);
  	
  	//2. JWT 생성
  	Long expiredMs = 1000L * 60 * 60; //1시간
  	String userJwt = jwtUtil.createJwt(uDTO, expiredMs);
  	
  	//3. 쿠키 생성
    ResponseCookie cookie = ResponseCookie.from("Authorization", userJwt)
	    .httpOnly(true)// JS 접근 불가
	    .secure(false) // HTTPS 환경에서만 동작 (개발시에는 false)
	    .sameSite("Strict") // CSRF 방지
	    .path("/") // 전체 경로에 대해 쿠키 전송
	    .maxAge(Duration.ofHours(1))// 쿠키 1시간 유지(JWT가 1시간이다)
	    .build();
    
    response.addHeader("Set-Cookie", cookie.toString()); //사용자 쿠키에 JWT 첨부
  	
    //addAttribute는 URL에 붙여서 전달 → 브라우저 주소창에 노출됨
    //addFlashAttribute는 세션에 임시 저장 → 노출 안 됨, 리다이렉트 후 한 번만 사용 가능
    if(uDTO.getRole().equals("ROLE_CORP")) {
    	return "redirect:/corp/main"; //기업회원은 로그인 후 기업메인페이지로 이동
    }
    
    return "redirect:/"; //로그인 후 메인페이지로 이동
  }
*/  
  
  /**
   * 일반회원 가입 페이지로 이동
   * @return
   */
  @GetMapping("/user/join")
  public String goJoinUserForm() {
    return "login/joinUserForm";
  }
  
  /**
   * 기업회원 가입 페이지로 이동
   * @return
   */
  @GetMapping("/corp/join")
  public String goJoinCorpForm() {
    return "login/joinCorpForm";
  }
  
  /**
   * 일반회원 가입 프로세스
   */
  @PostMapping("/user/joinProcess")
	public String userJoinProcess(@ModelAttribute UserDTO uDTO, HttpServletRequest request, Model model, RedirectAttributes redirectAttr) {
    // model에 Entity를 담는건 아니야. DTO를 담아. Entity는 보안문제도 있고 민감한 객체야. 
    ljs.registerUser(uDTO);
    //redirect:로 리다이렉트되면 Model에 담긴 값은 유지되지 않아
    //대신 RedirectAttributes.addFlashAttribute()를 써야 유지됨. -> 이건 세션에 잠깐 값을 저장했다가, 리다이렉트 이후 자동으로 한 번만 Model에 옮겨주는 기능. 리다이렉트 전용 Model!
  	redirectAttr.addFlashAttribute("msg", "회원가입이 완료되었습니다. 로그인 해주세요.");
  	
  	return "redirect:/login";
  }
  
  /**
   * 기업회원 가입 프로세스 (파일저장은 개발시 임시로 프로젝트 내에 저장하지만, 배포시에는 C:/upload/... 같은 외부 폴더 사용할 것!)
   */
  @PostMapping("/corp/joinProcess")
	public String corpJoinProcess(@ModelAttribute UserCorpDTO ucDTO, @RequestParam("upfile") MultipartFile file, RedirectAttributes redirectAttr) {
    
    Long corpNo = ucDTO.getCorpNo();
    
    String projectPath = new File("").getAbsolutePath(); // 현재 프로젝트 루트
    String resourcePath = projectPath + "/src/main/resources/static/corp/biz_cert";
    
    // 디렉토리가 없으면 생성
    File folder = new File(resourcePath);
    if (!folder.exists()) {
        folder.mkdirs();
    }

    String newFileName = null;
    File saveFile = null;
    
    if(!file.isEmpty()) {
      String originFileName = file.getOriginalFilename();
      //저장할 파일명: cert_사업자번호_파일이름
      newFileName = "cert_" + corpNo + "_" + originFileName;
      
      // 최종 경로
      saveFile = new File(resourcePath + File.separator + newFileName);
      
      try {
        file.transferTo(saveFile); //실제 저장
        ucDTO.setUpfileName(originFileName); //파일명저장 
        
      } catch (IOException e) {
        e.printStackTrace();
        redirectAttr.addAttribute("msg", "파일 업로드 실패");
        return "redirect:/corp/join"; // 회원가입 페이지로 다시 이동
      }
    } // end if file
    
    // DB 저장 처리
    try {
      ljs.registerCorp(ucDTO, newFileName);
      redirectAttr.addFlashAttribute("msg", "회원가입이 완료되었습니다.");
      return "redirect:/login";

    } catch (Exception e) {
      e.printStackTrace();

      // DB 저장 실패 시 업로드된 파일 삭제
      if (saveFile != null && saveFile.exists()) {
        boolean deleted = saveFile.delete();
        if (deleted) {
          System.out.println("디버깅) 업로드된 파일 삭제 완료: " + newFileName);
        } else {
          System.out.println("디버깅) 업로드된 파일 삭제 실패: " + newFileName);
        }
      }

			/*
			 * // 구체적인 에러 메시지 설정 String errorMsg = "회원가입 처리 중 오류가 발생했습니다."; if (e instanceof
			 * IllegalArgumentException) { errorMsg = e.getMessage(); // 예: "기업이 존재하지 않습니다."
			 * } else if (e.getMessage() != null && e.getMessage().contains("Duplicate")) {
			 * errorMsg = "이미 존재하는 사업자번호 또는 이메일입니다."; }
			 * redirectAttr.addFlashAttribute("msg", errorMsg);
			 */      
//      return "redirect:/corp/join"; // 회원가입 페이지로 다시 이동\
      
      throw e; //이거 안던져주면 GlobalExceptionHandler의 @ControllerAdvice까지 못가고 증발해
    }
}
  /** 이메일 구현시 추가할거
   * @PostMapping("/mail/sendVfCode")
      public ResponseEntity<String> sendVerificationCode(
          @RequestParam("to") String email,
          @RequestParam(value = "action", required = false) String action
      ) {
          // 이메일 발송 로직
          try {
              // 이메일 발송 처리
              return ResponseEntity.ok("success");
          } catch (Exception e) {
              return ResponseEntity.status(500).body("fail");
          }
      }
   */
  
  /**
   * 이메일 중복 체크
   */
  @GetMapping("/ckEmailDupl")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> chEmailDupl(@RequestParam("email") String email) {
  	boolean isDuple = ljs.chkEmailDupl(email);
  	Map<String, Object> reponse = new HashMap<String, Object>();
  	reponse.put("duplicate", isDuple);
  	reponse.put("message", isDuple ? "이미 사용 중입니다." : "");
  	return ResponseEntity.ok(reponse);
  }
  
  @GetMapping("/corp/testForm")
  public String testForm() {
    return "login/joinCorpFormOnlyHTML";
  }
  
  
  
  
  /**
   * 대량의 일반회원 가입 프로세스 (JSON)
   */
  @PostMapping("/user/joinProcessJson")
	public ResponseEntity<?> userJoinProcess(@RequestBody UserDTO uDTO) {
    // model에 Entity를 담는건 아니야. DTO를 담아. Entity는 보안문제도 있고 민감한 객체야. 
    try {
    	ljs.registerUser(uDTO);
    	return ResponseEntity.ok("등록 성공");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
  }
//===============================
//랜덤 이력서 생성용 새로운 메서드들
//===============================

//랜덤 이력서 생성을 위한 신규 이력서 생성 (userEmail 파라미터 받음)
@PostMapping("/user/tempresume/resumeSubmit")
@ResponseBody
public Map<String, String> tempResumeSubmit(@RequestParam("resumeData") String resumeDataJson,
                                         @RequestParam("userEmail") String userEmail) {
   Map<String, String> result = new HashMap<>();
   
   try {
       // 해당 이메일의 유저가 존재하는지 확인
       UserEntity user = userRepos.findById(userEmail).orElse(null);
       if (user == null) {
           result.put("result", "user_not_found");
           return result;
       }
       
       // ResumeRequestDTO로 변환
       ResumeRequestDTO rdd = objMapper.readValue(resumeDataJson, ResumeRequestDTO.class);
       
       // 신규 이력서 생성 (resumeSeq가 없으므로)
       int newResumeSeq = rServ.addResume(user);
       
       // basicInfo에 새로 생성된 resumeSeq 설정
       rdd.getBasicInfo().setResumeSeq(newResumeSeq);
       
       // 이력서 데이터 저장 (프로필 이미지는 null로 처리)
       rServ.modifyResume(rdd, null, newResumeSeq);
       
       result.put("result", "success");
       result.put("resumeSeq", String.valueOf(newResumeSeq));
       
   } catch (Exception e) {
       e.printStackTrace();
       result.put("result", "error");
       result.put("message", e.getMessage());
   }
   
   return result;
}  
//대량 랜덤 이력서 생성을 위한 배치 처리 메서드 (선택사항)
@PostMapping("/user/tempresume/batchCreate")
@ResponseBody
public Map<String, Object> batchCreateTempResumes(@RequestParam("startIndex") int startIndex,
                                               @RequestParam("endIndex") int endIndex) {
   Map<String, Object> result = new HashMap<>();
   List<String> successList = new ArrayList<>();
   List<String> failList = new ArrayList<>();
   
   try {
       for (int i = startIndex; i <= endIndex; i++) {
           String email = "testuser" + i + "@test.com";
           
           // 유저가 존재하는지 확인
           UserEntity user = userRepos.findById(email).orElse(null);
           if (user == null) {
               failList.add(email + " - 유저가 존재하지 않음");
               continue;
           }
           
           try {
               // 신규 이력서 생성
               int newResumeSeq = rServ.addResume(user);
               successList.add(email + " - Resume SEQ: " + newResumeSeq);
               
           } catch (Exception e) {
               failList.add(email + " - " + e.getMessage());
           }
       }
       
       result.put("result", "completed");
       result.put("successCount", successList.size());
       result.put("failCount", failList.size());
       result.put("successList", successList);
       result.put("failList", failList);
       
   } catch (Exception e) {
       e.printStackTrace();
       result.put("result", "error");
       result.put("message", e.getMessage());
   }
   
   return result;
}
  
  
  
}
