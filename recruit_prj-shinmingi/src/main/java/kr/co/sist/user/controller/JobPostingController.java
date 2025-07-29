package kr.co.sist.user.controller;


import java.text.NumberFormat;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.sist.jwt.CustomUser;
import kr.co.sist.login.UserRepository;
import kr.co.sist.user.dto.JobPostDTO;
import kr.co.sist.user.dto.NoticeDTO;
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
public class JobPostingController {
    
    private final JobPostingService jps;
    private final UserRepository ur;
    private final CipherUtil cu;
    private final  ResumeService rs;
    private final RecentViewService recentViewService;
    private final UserNoticeService noticeService;
	    
    //개발자채용
    @GetMapping("/user/job_posting/job_posting")
    public String getJobPostings(@RequestParam(required = false) Integer jobPostingSeq, Model model) {
        List<JobPostDTO> jobPostingList = jps.getJobPostings(jobPostingSeq);
        model.addAttribute("jobPostingList", jobPostingList);
        return "user/job_posting/job_posting";
    }

    //공고상세보기
    @GetMapping("/user/job_posting/job_posting_detail")
    public String JobPostingDetailPage(@RequestParam(required = false) Integer jobPostingSeq,
                                       @AuthenticationPrincipal CustomUser userInfo,
                                       Model model) {
    	
        JobPostDTO jDto = jps.findById(jobPostingSeq);
        
        // 조회수 증가
        if (jDto != null) {
            jps.incrementViewCount(jobPostingSeq); // 조회수 증가
        }

        // 공고 데이터 모델에 추가
        model.addAttribute("jDto", jDto);
        
        NumberFormat numberFormat = NumberFormat.getInstance();
        String corpAvgSal = numberFormat.format(jDto.getCorpAvgSal()) + "만원";
        model.addAttribute("corpAvgSal", corpAvgSal);
        
        
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

        return "user/job_posting/job_posting_detail";
    }
    
    
    @GetMapping("/user/job_postings")
    public String showMainPage(Model model) {
        try {
            List<JobPostDTO> randomPostings = jps.getRandomJobPostings();
            List<JobPostDTO> popularPostings = jps.getPopularJobPostings();
            
            
            model.addAttribute("randomPostings", randomPostings);
            model.addAttribute("popularPostings", popularPostings);
            
            List<NoticeDTO> noticeList = noticeService.getLatestNotices();
            model.addAttribute("noticeList", noticeList);
            
            return "user/main_page";
        } catch (Exception e) {
            model.addAttribute("error", "데이터를 불러오는 중 오류가 발생했습니다.");
            return "error/error";
        }
    }
    
    
}
