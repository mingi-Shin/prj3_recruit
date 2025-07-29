package kr.co.sist.admin.resume;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.sist.user.dto.ResumeDTO;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private adminResumeMapper rm;

    @Override
    public List<ResumeDTO> selectResume() {
        return rm.selectResume();
    }
    @Override
    public List<Map<String, Object>> selectResume2() {
    	return rm.selectResume2();
    }
    @Override
    public List<Map<String, Object>> searchResume(Integer resume_seq,String name) {
    	return rm.searchResume(resume_seq,name);
    }
    
}