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
import kr.co.sist.user.dto.ScrapDTO;
import kr.co.sist.user.service.ScrapService;


@RestController
public class ScrapController {

    @Autowired
    private ScrapService scrapService;

    // 스크랩 추가
    @PostMapping("/scrap/add")
    public Map<String, Object> addScrap(@RequestBody ScrapDTO scrapDTO,
                                       @AuthenticationPrincipal CustomUser userInfo) {
        Map<String, Object> result = new HashMap<>();
        if (userInfo == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        scrapDTO.setEmail(userInfo.getEmail());

        try {
            boolean already = scrapService.isAlreadyScrapped(scrapDTO.getJobPostingSeq(), scrapDTO.getEmail());
            if (already) {
                result.put("success", false);
                result.put("message", "이미 스크랩된 공고입니다.");
                return result;
            }

            scrapService.addScrap(scrapDTO);
            result.put("success", true);
            result.put("message", "스크랩이 추가되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "스크랩 추가 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        return result;
    }

    // 스크랩 제거
    @PostMapping("/scrap/remove")
    public Map<String, Object> removeScrap(@RequestBody ScrapDTO scrapDTO,
                                           @AuthenticationPrincipal CustomUser userInfo) {
        Map<String, Object> result = new HashMap<>();
        if (userInfo == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        String email = userInfo.getEmail();
        Integer jobPostingSeq = scrapDTO.getJobPostingSeq();

        try {
            boolean removed = scrapService.removeScrap(jobPostingSeq, email);
            if (removed) {
                result.put("success", true);
                result.put("message", "스크랩이 취소되었습니다.");
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
    
    
    @GetMapping("/scrap/check")
    public Map<String, Object> checkScrap(@RequestParam Integer jobPostingSeq,
                                          @AuthenticationPrincipal CustomUser userInfo) {
        Map<String, Object> result = new HashMap<>();
        if (userInfo == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        boolean already = scrapService.isAlreadyScrapped(jobPostingSeq, userInfo.getEmail());
        result.put("success", true);
        result.put("scrapped", already);
        return result;
    }

}