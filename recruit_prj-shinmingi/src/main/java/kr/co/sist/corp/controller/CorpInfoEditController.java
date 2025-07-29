package kr.co.sist.corp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.sist.corp.dto.CorpEntity;
import kr.co.sist.corp.service.CorpEditService;
import kr.co.sist.jwt.CustomUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/corp/info/")
public class CorpInfoEditController {

    @Autowired
    private CorpEditService corpService;

    // 기업정보 수정 페이지 조회
    @GetMapping("/edit")
    public String editForm(Model model, @AuthenticationPrincipal CustomUser user) {
        // 세션에서 기업번호 가져오기 (로그인한 기업의 번호)
        Long corpNo = user.getCorpNo(); // 실제 구현에서는 세션에서 가져와야 함
        
        CorpEntity corpInfo = corpService.getCorpInfo(corpNo);
        
        model.addAttribute("corpInfo", corpInfo);
        
        return "corp/corp_info_edit";
    }

   
    // 기업정보 수정 처리
    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> updateCorpInfo(@ModelAttribute CorpEntity corpEntity, @AuthenticationPrincipal CustomUser user) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 현재 로그인한 기업 번호 설정
            corpEntity.setCorpNo(user.getCorpNo());
            
            // 기업정보 수정
            corpService.updateCorpInfo(corpEntity);
            
            response.put("success", true);
            response.put("message", "기업정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "수정 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return response;
    }

    // 기업정보 조회 페이지
    @GetMapping("/view")
    public String viewCorpInfo(Model model, @AuthenticationPrincipal CustomUser user) {
        Long corpNo = user.getCorpNo();
        CorpEntity corpInfo = corpService.getCorpInfo(corpNo);
        model.addAttribute("corpInfo", corpInfo);
        
        return "corp/corp_info_view";
    }
}
