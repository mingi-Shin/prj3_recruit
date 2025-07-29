package kr.co.sist.user.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.sist.jwt.CustomUser;
import kr.co.sist.login.UserRepository;
import kr.co.sist.user.dto.JobPostDTO;
import kr.co.sist.user.dto.NoticeDTO;
import kr.co.sist.user.dto.UserDTO;
import kr.co.sist.user.entity.UserEntity;
import kr.co.sist.user.service.JobPostingService;
import kr.co.sist.user.service.MainPageService;
import kr.co.sist.user.service.UserNoticeService;
import kr.co.sist.util.CipherUtil;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainPageController {
    
    private final UserRepository ur;
    private final CipherUtil cu;
    private final JobPostingService jps; 
    private final MainPageService mainPageService;
    private final UserNoticeService noticeService;
    
    @GetMapping("/user/main_page")
    public String MainPage(@AuthenticationPrincipal CustomUser userInfo, Model model) {
        
        
        if (userInfo != null) {
            // 사용자 정보 가져오기
            UserEntity userEntity = ur.findById(userInfo.getEmail()).orElse(null);
            if(userEntity == null) { 
                return "redirect:/accessDenied"; 
            }
            
            // 기업회원이면 사용자 메인페이지 접근 차단
            if (userEntity.getCorpEntity() != null) {
                return "redirect:/corp/main";
            }
            
            // UserDTO로 변환 후 model에 추가
            UserDTO user = new UserDTO(userEntity);
            model.addAttribute("user", user);
        }
        
        
        List<NoticeDTO> noticeList = noticeService.getLatestNotices();
        model.addAttribute("noticeList", noticeList);

        
        return "user/main_page";
    }//MainPage
    
    
    
    //검색
    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
    	
    	List<JobPostDTO> jobList=mainPageService.searchJobPostings(keyword);
    	
    	model.addAttribute("jobList",jobList);
    	model.addAttribute("keyword",keyword);
    	
    	return "user/searchResult";
    	
    }//search


    @ResponseBody
    @GetMapping("/autocomplete")
    public List<String> autoComplete(@RequestParam("term") String term){
    	
      return mainPageService.getAutoCompleteSuggestions(term);
    }
    
 
    
}