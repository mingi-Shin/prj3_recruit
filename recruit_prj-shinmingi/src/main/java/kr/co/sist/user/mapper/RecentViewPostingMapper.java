package kr.co.sist.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.sist.user.dto.RecentViewPostingDTO;

@Mapper
public interface RecentViewPostingMapper {
	
	//최근 본 공고
	RecentViewPostingDTO selectRecentView(@Param("email") String email,
																				@Param("jobPostingSeq") Integer jobPostingSeq);
	

	void insertRecentView(RecentViewPostingDTO dto);
	
	 // 기존 기록의 시간 업데이트
  void updateRecentViewTime(RecentViewPostingDTO dto); 
  
  void deleteOldRecentViews(@Param("email") String email, @Param("maxRecords") int maxRecords);
  
}
