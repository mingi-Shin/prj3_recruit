package kr.co.sist.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.sist.jwt.CustomUser;
import kr.co.sist.user.dto.CorpScrapDTO;
import kr.co.sist.user.service.CorpScrapService;

@RestController
public class CorpScrapController {
    
    @Autowired
    private CorpScrapService corpScrapService;

    // 기업 스크랩 추가
    @PostMapping("/corpScrap/add")
    public Map<String, Object> addCorpScrap(@RequestBody CorpScrapDTO corpScrapDTO,
                                            @AuthenticationPrincipal CustomUser userInfo) {
        Map<String, Object> result = new HashMap<>();
        
        if (userInfo == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return result;
        }
        
        corpScrapDTO.setEmail(userInfo.getEmail());
        
        try {
            boolean already = corpScrapService.isAlreadyScrapped(corpScrapDTO.getCorpNo(), corpScrapDTO.getEmail());
            if (already) {
                result.put("success", false);
                result.put("message", "이미 스크랩된 기업입니다.");
                return result;
            }
            
            corpScrapService.addCorpScrap(corpScrapDTO);
            result.put("success", true);
            result.put("message", "기업 스크랩이 추가되었습니다.");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "스크랩 추가 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        
        return result;
    }

    // 기업 스크랩 제거
    @PostMapping("/corpScrap/remove")
    public Map<String, Object> removeCorpScrap(@RequestBody CorpScrapDTO corpScrapDTO,
                                               @AuthenticationPrincipal CustomUser userInfo) {
        Map<String, Object> result = new HashMap<>();
        
        if (userInfo == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return result;
        }
        
        String email = userInfo.getEmail();
        long corpNo = corpScrapDTO.getCorpNo();
        
        try {
            boolean removed = corpScrapService.removeCorpScrap(corpNo, email);
            if (removed) {
                result.put("success", true);
                result.put("message", "기업 스크랩이 취소되었습니다.");
            } else {
                result.put("success", false);
                result.put("message", "스크랩 정보가 존재하지 않습니다.");
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "스크랩 취소 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        
        return result;
    }
    
    @GetMapping("/corpScrap/check")
    public Map<String, Object> checkScrap(@RequestParam Integer corpNo,
                                          @AuthenticationPrincipal CustomUser userInfo) {
        Map<String, Object> result = new HashMap<>();
        if (userInfo == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        boolean already = corpScrapService.isAlreadyScrapped(corpNo, userInfo.getEmail());
        result.put("success", true);
        result.put("scrapped", already);
        return result;
    }
}

