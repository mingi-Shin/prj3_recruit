package kr.co.sist.faq;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/help/api") // ✅ HTML과 경로 맞춤
public class FFaqController {
    private final FFaqService faqService;
    
    public FFaqController(FFaqService faqService) {
        this.faqService = faqService;
    }
    
    @GetMapping("/faqs") // ✅ HTML에서 호출하는 경로와 일치
    public ResponseEntity<Map<String, List<FFaqDto>>> getFaqs(@RequestParam String userType) {
        System.out.println("🔍 Controller - 요청된 userType: " + userType);
        Map<String, List<FFaqDto>> result = faqService.getFaqsByUserType(userType);
        return ResponseEntity.ok(result);
    }
}