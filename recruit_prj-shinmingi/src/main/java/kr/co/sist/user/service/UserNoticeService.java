package kr.co.sist.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.sist.user.dto.NoticeDTO;
import kr.co.sist.user.mapper.UserNoticeMapper;

@Service
public class UserNoticeService {
	
	@Autowired
	private UserNoticeMapper noticeMapper;
	
	 public List<NoticeDTO> getLatestNotices() {
     return noticeMapper.selectLatestNotices();
 }

	 public List<NoticeDTO> getAllNotice() {
		 return noticeMapper.selectAllNotices();
	 }
}
