package kr.co.sist.admin.faq;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FaqService {

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<FaqEntity> getAllFaqs() {
        return faqRepository.findAll();
    }

    public Optional<FaqEntity> getFaqById(int faqSeq) {
        return faqRepository.findById(faqSeq);
    }

    @Transactional
    public FaqEntity createFaq(FaqEntity faq) {
        return faqRepository.save(faq);
    }

    @Transactional
    public FaqEntity updateFaq(FaqEntity faq) {
        return faqRepository.save(faq);
    }

    @Transactional
    public void deleteFaq(int faqSeq) {
        faqRepository.deleteById(faqSeq);
   }
   /* public List<FaqEntity> getFaqsFilteredByUserType(String userType) {
      List<FaqEntity> allFaqs = faqRepository.findAll();

      // 서비스에서 userType 키워드 기반 필터링
      return allFaqs.stream()
          .filter(faq -> {
              String title = faq.getTitle().toLowerCase();
              String content = faq.getContent().toLowerCase();
              return (userType.equals("user") && title.contains("일반")) ||
                     (userType.equals("company") && title.contains("기업"));
          })
          .toList();
  }*/
        public List<FaqEntity> getFaqsFilteredByUserType(String userType) {
          return faqRepository.findAll();
      }
}
