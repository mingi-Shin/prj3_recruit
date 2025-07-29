package kr.co.sist.corp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kr.co.sist.corp.dto.CorpEntity;
import kr.co.sist.corp.service.CorpImageService;
import kr.co.sist.jwt.CustomUser;
import kr.co.sist.login.CorpRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/corp")
@RequiredArgsConstructor
public class CorpImageController {

    private final CorpImageService corpImageService;
    private final CorpRepository corpRepos;

    /**
     * 이미지 등록/수정 페이지 이동
     */
    @GetMapping("/image/upload")
    public String imageForm(@AuthenticationPrincipal CustomUser userInfo, Model model) {
        
        // 기업 회원이 아니면 접근금지
        boolean hasCorpAuth = userInfo.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_CORP".equals(auth.getAuthority()));
        if (!hasCorpAuth) {
            return "redirect:/accessDenied";
        }

        try {
            CorpEntity corp = corpRepos.findById(userInfo.getCorpNo()).orElse(null);
            if (corp == null) {
                model.addAttribute("error", "존재하지 않는 회사입니다.");
                return "corp/image_upload";
            }

            model.addAttribute("corpInfo", corp);
            model.addAttribute("corpNo", corp.getCorpNo());
            
        } catch (Exception e) {
            model.addAttribute("error", "페이지 로딩 중 오류가 발생했습니다.");
        }
        
        return "corp/image_upload";
    }

    /**
     * 로고 이미지 업로드/수정
     */
    @PostMapping("/image/logo")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadLogo(
            @RequestParam("logoFile") MultipartFile logoFile,
            @AuthenticationPrincipal CustomUser userInfo) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 기업 회원이 아니면 접근금지
            boolean hasCorpAuth = userInfo.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_CORP".equals(auth.getAuthority()));
            if (!hasCorpAuth) {
                response.put("success", false);
                response.put("message", "접근 권한이 없습니다.");
                return ResponseEntity.ok(response);
            }

            CorpEntity corp = corpRepos.findById(userInfo.getCorpNo()).orElse(null);
            if (corp == null) {
                response.put("success", false);
                response.put("message", "존재하지 않는 회사입니다.");
                return ResponseEntity.ok(response);
            }

            // 파일 검증
            if (logoFile == null || logoFile.isEmpty()) {
                response.put("success", false);
                response.put("message", "로고 파일을 선택해주세요.");
                return ResponseEntity.ok(response);
            }

            // 로고 업로드/수정
            String logoPath = corpImageService.uploadLogo(corp.getCorpNo(), logoFile);
            
            response.put("success", true);
            response.put("message", "로고가 성공적으로 업로드되었습니다.");
            response.put("logoPath", logoPath);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "로고 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 회사 이미지 업로드/수정
     */
    @PostMapping("/image/corp")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadCompanyImage(
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
            @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
            @AuthenticationPrincipal CustomUser userInfo) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 기업 회원이 아니면 접근금지
            boolean hasCorpAuth = userInfo.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_CORP".equals(auth.getAuthority()));
            if (!hasCorpAuth) {
                response.put("success", false);
                response.put("message", "접근 권한이 없습니다.");
                return ResponseEntity.ok(response);
            }

            CorpEntity corp = corpRepos.findById(userInfo.getCorpNo()).orElse(null);
            if (corp == null) {
                response.put("success", false);
                response.put("message", "존재하지 않는 회사입니다.");
                return ResponseEntity.ok(response);
            }

            // 적어도 하나의 파일은 존재해야 함
            if ((logoFile == null || logoFile.isEmpty()) && 
                (imageFiles == null || imageFiles.length == 0)) {
                response.put("success", false);
                response.put("message", "최소 하나의 파일을 업로드해주세요.");
                return ResponseEntity.ok(response);
            }

            // 로고 파일 업로드 처리 (로고가 있을 경우)
            if (logoFile != null && !logoFile.isEmpty()) {
                String logoPath = corpImageService.uploadLogo(corp.getCorpNo(), logoFile);
                response.put("logoPath", logoPath);
            }

            // 기업 이미지 파일 업로드 처리
            if (imageFiles != null && imageFiles.length > 0) {
                List<String> imagePaths = new ArrayList<>();
                for (MultipartFile imageFile : imageFiles) {
                    if (!imageFile.isEmpty()) {
                        String imagePath = corpImageService.uploadCompanyImage(corp.getCorpNo(), imageFile);
                        imagePaths.add(imagePath);
                    }
                }
                response.put("imagePaths", imagePaths);
            }
            
            response.put("success", true);
            response.put("message", "이미지가 성공적으로 업로드되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "이미지 업로드 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 로고 삭제
     */
    @PostMapping("/image/logo/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteLogo(@AuthenticationPrincipal CustomUser userInfo) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 기업 회원이 아니면 접근금지
            boolean hasCorpAuth = userInfo.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_CORP".equals(auth.getAuthority()));
            if (!hasCorpAuth) {
                response.put("success", false);
                response.put("message", "접근 권한이 없습니다.");
                return ResponseEntity.ok(response);
            }

            CorpEntity corp = corpRepos.findById(userInfo.getCorpNo()).orElse(null);
            if (corp == null) {
                response.put("success", false);
                response.put("message", "존재하지 않는 회사입니다.");
                return ResponseEntity.ok(response);
            }

            // 로고 삭제
            corpImageService.deleteLogo(corp.getCorpNo());
            
            response.put("success", true);
            response.put("message", "로고가 삭제되었습니다.");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "로고 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 회사 이미지 삭제
     */
    @PostMapping("/image/corp/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteCompanyImage(@AuthenticationPrincipal CustomUser userInfo) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 기업 회원이 아니면 접근금지
            boolean hasCorpAuth = userInfo.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_CORP".equals(auth.getAuthority()));
            if (!hasCorpAuth) {
                response.put("success", false);
                response.put("message", "접근 권한이 없습니다.");
                return ResponseEntity.ok(response);
            }

            CorpEntity corp = corpRepos.findById(userInfo.getCorpNo()).orElse(null);
            if (corp == null) {
                response.put("success", false);
                response.put("message", "존재하지 않는 회사입니다.");
                return ResponseEntity.ok(response);
            }

            // 회사 이미지 삭제
            corpImageService.deleteCompanyImage(corp.getCorpNo());
            
            response.put("success", true);
            response.put("message", "회사 이미지가 삭제되었습니다.");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "이미지 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}