package kr.co.sist.notice;
import kr.co.sist.notice.HelpNoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpNoticeRepository extends JpaRepository<HelpNoticeEntity, Long> {
    List<HelpNoticeEntity> findTop10ByOrderByRegsDateDesc(); // 최근 10개 공지사항
}