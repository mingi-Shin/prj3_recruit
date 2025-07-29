package kr.co.sist.corp.service;

import kr.co.sist.corp.dto.ResumeDetailDTO;

public interface ResumeDetailService {
		ResumeDetailDTO getResumeDetail(Long resumeSeq, Long corpNo);
}
