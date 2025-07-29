package kr.co.sist.admin.faq;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/faq")
public class FaqRestController {

    private final FaqService faqService;

    public FaqRestController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping
    public List<FaqDTO> getFaqList() {
        return faqService.getAllFaqs()
                         .stream()
                         .map(FaqDTO::new)
                         .collect(Collectors.toList());
    }

    @GetMapping("/{faqSeq}")
    public ResponseEntity<FaqEntity> getFaq(@PathVariable int faqSeq) {
        Optional<FaqEntity> faq = faqService.getFaqById(faqSeq);
        return faq.map(ResponseEntity::ok)
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public FaqEntity createFaq(@RequestBody FaqEntity faq) {
        return faqService.createFaq(faq);
    }

    @PutMapping("/{faqSeq}")
    public ResponseEntity<FaqEntity> updateFaq(@PathVariable int faqSeq, @RequestBody FaqEntity faq) {
        Optional<FaqEntity> existingFaq = faqService.getFaqById(faqSeq);
        if (!existingFaq.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        faq.setFaqSeq(faqSeq);
        FaqEntity updatedFaq = faqService.updateFaq(faq);
        return ResponseEntity.ok(updatedFaq);
    }

    @DeleteMapping("/{faqSeq}")
    public ResponseEntity<Void> deleteFaq(@PathVariable int faqSeq) {
        Optional<FaqEntity> existingFaq = faqService.getFaqById(faqSeq);
        if (!existingFaq.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        faqService.deleteFaq(faqSeq);
        return ResponseEntity.noContent().build();
    }
}
