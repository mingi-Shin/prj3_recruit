package kr.co.sist.admin.notice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class NoticeService {

    private final NoticeMapper noticeMapper;

    @Autowired
    public NoticeService(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    // 공지사항 목록 조회
    public List<Notice> getNoticeList(String searchType, String keyword) {
        return noticeMapper.getNotices(searchType, keyword);
    }

    // 공지사항 조회
    public Notice getNoticeById(int noticeSeq) {
        return noticeMapper.getNoticeById(noticeSeq);
    }

    // 공지사항 작성
    @Transactional
    public void createNotice(Notice notice) {
      if (notice.getRegsDate() == null || notice.getRegsDate().isEmpty()) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        notice.setRegsDate(now);
    }
        noticeMapper.insertNotice(notice);
    }

    // 공지사항 수정
    public void updateNotice(Notice notice) {
        noticeMapper.updateNotice(notice);
    }
    
    // 공지사항 다중 삭제 (새로 추가)
    @Transactional
    public void deleteNotices(List<Integer> noticeSeqs) {
        if (noticeSeqs != null && !noticeSeqs.isEmpty()) {
            noticeMapper.deleteNotices(noticeSeqs);
        }
    }
}
