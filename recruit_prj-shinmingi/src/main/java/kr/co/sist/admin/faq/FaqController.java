package kr.co.sist.admin.faq;

import java.util.List;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.sist.jwt.CustomAdmin;


@Controller
@RequestMapping("admin/admin_faq")
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping
    public String faqList(Model model) {
        List<FaqEntity> faqs = faqService.getAllFaqs();
        model.addAttribute("faqList", faqs);
        return "admin/admin_faq"; // src/main/resources/templates/admin/faqList.html (Thymeleaf 뷰)
    }
    

    
    @GetMapping("/save")
    public String showFaqForm(@RequestParam(required = false) Integer faqSeq, Model model) {
        if (faqSeq != null) {
            Optional<FaqEntity> faq = faqService.getFaqById(faqSeq);
            faq.ifPresent(value -> model.addAttribute("faq", value));
        }
        return "admin/admin_faq_form"; // 작성 폼 뷰 이름 (templates/admin/admin_faq_save.html)
    }
    
 // 추가: POST 요청 처리 메서드
    @PostMapping("/save")
    public String saveFaq(@ModelAttribute FaqEntity faq, RedirectAttributes redirectAttributes) {
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	CustomAdmin admin = (CustomAdmin) auth.getPrincipal(); // CustomAdmin으로 캐스팅
    	faq.setAdminId(admin.getAdmin()); // 또는 getAdminId() - 아래 설명 참고
        faqService.createFaq(faq); // 신규 등록 또는 수정 저장 처리

        redirectAttributes.addFlashAttribute("message", "FAQ가 성공적으로 저장되었습니다.");
        return "redirect:/admin/admin_faq"; // 저장 후 FAQ 리스트 페이지로 리다이렉트
    }

} 


