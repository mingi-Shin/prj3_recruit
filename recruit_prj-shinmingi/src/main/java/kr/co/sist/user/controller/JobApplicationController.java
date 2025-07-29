package kr.co.sist.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.sist.jwt.CustomUser;
import kr.co.sist.user.dto.AttachmentDTO;
import kr.co.sist.user.dto.AttahDTO;
import kr.co.sist.user.dto.JobPostDTO;
import kr.co.sist.user.dto.ResumeDTO;
import kr.co.sist.user.service.JobApplicationService;
import kr.co.sist.user.service.JobPostingService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;
    private final JobPostingService jps;
    
    /**
     * 로그인한 사용자의 이메일로 이력서 목록을 JSON 형태로 반환
     * - AJAX 등에서 호출하여 사용
     */
    @GetMapping("/user/resume")
    public ResponseEntity<List<ResumeDTO>> getResumes(@AuthenticationPrincipal CustomUser userInfo) {
        List<ResumeDTO> resumes = jobApplicationService.getResumesByEmail(userInfo.getEmail());
        return ResponseEntity.ok(resumes);
    }
    
    @GetMapping("/user/attachments")
    public ResponseEntity<List<AttahDTO>> getAttachments(@AuthenticationPrincipal CustomUser userInfo){
        List<AttahDTO> attachments = jobApplicationService.getAttachmentsByEmail(userInfo.getEmail());
        return ResponseEntity.ok(attachments);
    }
    
    
    /**
     * 지원하기 처리: 선택한 이력서와 공고로 지원 등록
     * - 성공 시 "지원이 완료되었습니다." 반환
     * - 실패 시 에러 메시지와 400 상태 반환
     */
    @PostMapping("/apply")
    public ResponseEntity<String> applyToJob(
            @RequestParam("resumeSeq") Integer resumeSeq,
            @RequestParam("jobPostingSeq") Integer jobPostingSeq,
            @RequestParam(value = "selectedAttachments", required = false) List<Integer> attachmentSeqs,
            @AuthenticationPrincipal CustomUser userInfo) {
    	
        try {
            JobPostDTO jobPost = jps.findById(jobPostingSeq);
            if (jobPost.getIsEnded().equals("Y")) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("마감된 공고입니다. 지원할 수 없습니다.");
            }
            
            //첨부파일과 함께 지원하기
            jobApplicationService.applyToJobWithSelectedAttachments(resumeSeq, jobPostingSeq, userInfo.getEmail(), attachmentSeqs);
            return ResponseEntity.ok("지원이 완료되었습니다.");              
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("지원 실패: " + (e.getMessage() != null ? e.getMessage() : "알 수 없는 오류"));
        }
    }

    /**
     * 타임리프로 렌더링하는 이력서 목록 페이지
     * - Model에 이력서 목록 넣고 user/resume-list.html 뷰 반환
     */
    @GetMapping("/user/resume/resume_form")
    public String showResumeList(@AuthenticationPrincipal CustomUser userInfo, Model model) {
        String email = userInfo.getEmail();
        List<ResumeDTO> resumes = jobApplicationService.getResumesByEmail(email);
        model.addAttribute("resumes", resumes);
        
        return "user/resume-list";
    }
    
    
    
}
