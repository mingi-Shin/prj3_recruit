package kr.co.sist.admin.ask;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminInquiryMapper {
	
	public List<AdminInquiryDTO> selectSearchAsks(Map<String, Object> map);
	
	int countSearch(Map<String, Object> map);
	
	int deleteAsk(Map<String,Object> map);
	
	int deleteInquiry(Map<String, Object> map);
	
	int updateInquiry(Map<String, Object> map);
	
	public AdminInquiryDTO selectSearchAsk(int askSeq);
}
