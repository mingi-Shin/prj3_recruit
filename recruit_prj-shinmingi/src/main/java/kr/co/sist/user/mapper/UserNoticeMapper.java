package kr.co.sist.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.sist.user.dto.NoticeDTO;

@Mapper
public interface UserNoticeMapper {
	
  List<NoticeDTO> selectLatestNotices();
  List<NoticeDTO> selectAllNotices();

}
