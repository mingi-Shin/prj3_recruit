package kr.co.sist.notice;


import kr.co.sist.notice.HelpNoticeDTO;
import kr.co.sist.notice.HelpNoticeRepository;
import kr.co.sist.notice.HelpNoticeEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HelpNoticeService {

    private final HelpNoticeRepository noticeRepository;

    public HelpNoticeService(HelpNoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public List<HelpNoticeDTO> getRecentNotices() {
        List<HelpNoticeEntity> entities = noticeRepository.findTop10ByOrderByRegsDateDesc();
        return entities.stream()
                .map(HelpNoticeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public HelpNoticeDTO getNoticeDetail(Long noticeSeq) {
        return noticeRepository.findById(noticeSeq)
                .map(HelpNoticeDTO::fromEntity)
                .orElse(null);
    }
}