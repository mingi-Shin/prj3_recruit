package kr.co.sist.admin.ask;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class AdminInquiryService {
	
	private final AdminInquiryMapper adminInquiryMapper;
	
	public AdminInquiryService(AdminInquiryMapper adminInquiryMapper) {
		this.adminInquiryMapper = adminInquiryMapper;
	}
	
	public List<AdminInquiryDTO> getInquirys(Map<String, Object> map) {
		
		return adminInquiryMapper.selectSearchAsks(map);
	}
	
	public AdminInquiryDTO getInquiry(int ask_seq){
		
		return adminInquiryMapper.selectSearchAsk(ask_seq);
	}
	
	public int countSearch(Map<String, Object> map) {
		return adminInquiryMapper.countSearch(map);
	}

	public boolean deleteInquiry(Map<String,Object> map) {
		int count = adminInquiryMapper.deleteInquiry(map);
		return count > 0;
	}
	
	public boolean updateInquiry(Map<String,Object> map) {
		int count = adminInquiryMapper.updateInquiry(map);
		return count > 0;
	}

}
