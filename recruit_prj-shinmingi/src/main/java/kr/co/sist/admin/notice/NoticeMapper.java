package kr.co.sist.admin.notice;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NoticeMapper {

    // 공지사항 목록 조회
    List<Notice> getNotices(@Param("searchType") String searchType, 
                            @Param("keyword") String keyword);

    // 공지사항 상세 조회
    Notice getNoticeById(@Param("noticeSeq") int noticeSeq);

    // 공지사항 작성
    void insertNotice(Notice notice);

    // 공지사항 수정
    void updateNotice(Notice notice);
    
    // 공지사항 단일 삭제
    void deleteNotice(@Param("noticeSeq") int noticeSeq);
    
    // 공지사항 다중 삭제 (새로 추가)
    void deleteNotices(List<Integer> noticeSeqs);
}
