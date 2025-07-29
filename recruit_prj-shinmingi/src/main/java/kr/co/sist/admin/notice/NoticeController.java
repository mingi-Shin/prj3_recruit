package kr.co.sist.admin.notice;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ResponseBody;
import kr.co.sist.jwt.CustomAdmin;

@Controller
public class NoticeController {
    private final NoticeService noticeService;
    
    @Autowired
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }
    
    // 공지사항 목록 페이지
    @GetMapping("/admin/notice_list")
    public String getNoticeList(@RequestParam(value = "searchType", defaultValue = "전체") String searchType,
                                @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                Model model) {
        model.addAttribute("notices", noticeService.getNoticeList(searchType, keyword));
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        return "admin/notice_list";
    }
    
    // AJAX용 JSON 공지사항 목록 반환
    @GetMapping("/admin/api/notice_list")
    @ResponseBody
    public List<Notice> getNoticeListJson(@RequestParam(value = "searchType", defaultValue = "전체") String searchType,
                                          @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        return noticeService.getNoticeList(searchType, keyword);
    }
    
    // 특정 공지사항 조회 (AJAX용)
    @GetMapping("/admin/api/notice/{noticeSeq}")
    @ResponseBody
    public Notice getNoticeById(@PathVariable("noticeSeq") int noticeSeq) {
        return noticeService.getNoticeById(noticeSeq);
    }
    

    
    // 공지사항 저장 처리 (AJAX용)
    @PostMapping("/admin/notice_save")
    @ResponseBody
    public String saveNotice(Notice notice) {
        try {
            // 현재 로그인한 관리자 정보 가져오기
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomAdmin admin = (CustomAdmin) auth.getPrincipal();
            // Notice 객체에 관리자 ID 세팅
            notice.setAdminId(admin.getUsername());
            
            if (notice.getNoticeSeq() != 0) {
                noticeService.updateNotice(notice);
            } else {
                noticeService.createNotice(notice);
            }
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
    
    // 공지사항 다중 삭제
    @PostMapping("/admin/notice_delete")
    @ResponseBody
    public String deleteNotices(@RequestParam("noticeSeqs") List<Integer> noticeSeqs) {
        try {
            noticeService.deleteNotices(noticeSeqs);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}