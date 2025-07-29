package kr.co.sist.corp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.sist.corp.service.ResumeDetailService;
import kr.co.sist.corp.service.TalentPoolService;
import kr.co.sist.jwt.CustomUser;

@RestController
@RequestMapping("/corp/resume_detail")
public class ResumeDetailController {

    private final ResumeDetailService resumeDetailService;
    private final TalentPoolService talentPoolService;

    public ResumeDetailController(ResumeDetailService resumeDetailService,
                                   TalentPoolService talentPoolService) {
        this.resumeDetailService = resumeDetailService;
        this.talentPoolService = talentPoolService;
    }

//    @GetMapping("/{resumeId}")
//    public ResumeDetailDTO getResumeDetail(@PathVariable("resumeId") Long resumeId) {
//        return resumeDetailService.getResumeDetail(resumeId);
//    }

    @PostMapping("/resume/view")
    public ResponseEntity<?> viewResume(@RequestParam("resumeSeq") Long resumeSeq,
                                        @AuthenticationPrincipal CustomUser corpInfo,Model model) {
        try {
            Long corpNo = corpInfo.getCorpNo();
            String corpName = corpInfo.getName();
            // 이력서 열람 기록 + 알림 메일 전송 처리
            talentPoolService.viewResume(resumeSeq, corpNo);
            
            // 이름을 모델에 추가하여 Thymeleaf 템플릿에서 사용할 수 있도록 함
            model.addAttribute("corpName", corpName);
            
            return ResponseEntity.ok().body("success");
        } catch (Exception e) {
            e.printStackTrace(); // 디버깅 시 원인 파악에 도움
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail");
        }
    }
}
