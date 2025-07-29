package kr.co.sist.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.sist.user.dto.TechStackDTO;
import kr.co.sist.user.mapper.JobStackMapper;

@Service
public class JobStackService {

    private final JobStackMapper jsm;

    // 생성자 주입을 통해 JobStackMapper를 주입받음
    @Autowired
    public JobStackService(JobStackMapper jsm) {
        this.jsm = jsm;  // 'this.jsm'과 'jsm'이 같으므로, this.jsm에 jsm을 할당
    }

    // 기술 스택을 기준으로 공고를 조회하는 메서드
    public List<TechStackDTO> getJobPostingsByTechStack(String stackName) {
        // JobStackMapper 인스턴스를 통해 메서드를 호출해야 함
        return jsm.selectJobPostingsByTechStack(stackName); 
    }
}
