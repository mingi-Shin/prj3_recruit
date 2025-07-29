package kr.co.sist.faq;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class FFaqService {
    private final FFaqRepository faqRepository;
    
    public FFaqService(FFaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }
    
    public Map<String, List<FFaqDto>> getFaqsByUserType(String userType) {
        System.out.println("🔍 Service - 요청된 userType: " + userType);
        
        // 모든 FAQ를 가져와서 Java에서 필터링
        List<FFaqEntity> allFaqs = faqRepository.findAllFaqs();
        System.out.println("🔍 Repository에서 조회된 전체 FAQ 개수: " + allFaqs.size());
        
        // userType에 따른 필터링 (제목이나 내용 기준)
        List<FFaqEntity> filteredFaqs = allFaqs.stream()
                .filter(faq -> isUserTypeFaq(faq, userType))
                .collect(Collectors.toList());
                
        System.out.println("🔍 필터링 후 FAQ 개수: " + filteredFaqs.size());
        
        List<FFaqDto> faqDtos = filteredFaqs.stream()
                .map(FFaqDto::fromEntity)
                .collect(Collectors.toList());
        
        return Map.of("all", faqDtos);
    }
    
    // FAQ가 특정 userType에 해당하는지 판단하는 메서드
    private boolean isUserTypeFaq(FFaqEntity faq, String userType) {
        if ("user".equals(userType)) {
            // 사용자용 FAQ 판단 로직 (예: 제목에 특정 키워드 포함)
            return !faq.getTitle().contains("기업") && !faq.getTitle().contains("채용");
        } else if ("company".equals(userType)) {
            // 기업용 FAQ 판단 로직
            return faq.getTitle().contains("기업") || faq.getTitle().contains("채용") || faq.getTitle().contains("구인");
        }
        return true; // 기본적으로 모든 FAQ 표시
    }
}