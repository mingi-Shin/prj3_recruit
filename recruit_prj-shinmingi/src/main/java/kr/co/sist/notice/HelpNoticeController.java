package kr.co.sist.notice;
import kr.co.sist.notice.HelpNoticeDTO;
import kr.co.sist.notice.HelpNoticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/help/api/notice")
public class HelpNoticeController {

    private final HelpNoticeService noticeService;

    public HelpNoticeController(HelpNoticeService noticeService) {
        this.noticeService = noticeService;
    }

    // 🔸 공지사항 목록
    @GetMapping("/list")
    public ResponseEntity<List<HelpNoticeDTO>> getNoticeList() {
        List<HelpNoticeDTO> notices = noticeService.getRecentNotices();
        return ResponseEntity.ok(notices);
    }

    // 🔸 공지사항 상세
    @GetMapping("/{noticeSeq}")
    public ResponseEntity<HelpNoticeDTO> getNoticeDetail(@PathVariable Long noticeSeq) {
        HelpNoticeDTO notice = noticeService.getNoticeDetail(noticeSeq);
        if (notice != null) {
            return ResponseEntity.ok(notice);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}