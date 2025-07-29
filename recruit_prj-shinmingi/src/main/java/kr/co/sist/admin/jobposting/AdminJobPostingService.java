package kr.co.sist.admin.jobposting;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.sist.corp.dto.JobPostingDTO;
import kr.co.sist.user.mapper.JobPostingMapper;

@Service
public class AdminJobPostingService {
	
	private final JobPostingMapper jobPostingMapper;
	
	public AdminJobPostingService(JobPostingMapper jobPostingMapper) {
		this.jobPostingMapper = jobPostingMapper;
	}
	
	public List<JobPostingDTO> getPostings(Map<String, Object> map){
		
		return jobPostingMapper.selectSearchPosting(map);
	}
	
	public int countSearch(Map<String, Object> map) {
		return jobPostingMapper.countSearch(map);
	}
	
	public boolean deletePosting(Map<String,Object> map) {
		int count = jobPostingMapper.deletePosting(map);
		return count > 0;
	}

}
