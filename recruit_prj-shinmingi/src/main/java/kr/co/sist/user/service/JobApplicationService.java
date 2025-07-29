package kr.co.sist.user.service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.sist.user.dto.AttachmentDTO;
import kr.co.sist.user.dto.AttahDTO;
import kr.co.sist.user.dto.JobApplicationDTO;
import kr.co.sist.user.dto.ResumeDTO;
import kr.co.sist.user.mapper.JobApplicationMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobApplicationService {
	
    private final JobApplicationMapper jobApplicationMapper;
    
    
    /**
     * 사용자의 이력서 목록조회
     * @param email
     * @return
     */
    public List<ResumeDTO> getResumesByEmail(String email) {
        return jobApplicationMapper.selectResumesByEmail(email);
    }
    

    
    /**
     * 사용자의 첨부파일 목록조회
     * @param email
     * @return
     */
    public List<AttahDTO> getAttachmentsByEmail(String email){
    	return jobApplicationMapper.selectAttachmentsByEmail(email);
    }
   
    
    @Transactional
    public void applyToJobWithSelectedAttachments(Integer resumeSeq, Integer jobPostingSeq, String email, List<Integer> attachmentSeqs) {
    	
        // 중복 지원 여부 확인
        JobApplicationDTO existing = jobApplicationMapper.selectExistingApplication(resumeSeq, jobPostingSeq);
        if (existing != null && (existing.getApplicationStatus() == null || existing.getApplicationStatus() != 2)) {
            throw new IllegalStateException("이미 해당 공고에 지원하셨습니다.");
        }
        
        //job_application 테이블에 지원 정보 삽입
        JobApplicationDTO jDTO = new JobApplicationDTO();
        jDTO.setResumeSeq(resumeSeq);
        jDTO.setJobPostingSeq(jobPostingSeq);
        jDTO.setApplicationStatus(0);
        jDTO.setApplicationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        jDTO.setIsRead("N");
        jDTO.setPassStage(0);
        
        jobApplicationMapper.insertJobApplicationVO(jDTO);
        Integer jobApplicationSeq = jDTO.getJobApplicationSeq();
        
        //선택된 첨부파일들이 있으면 application_attachment 테이블에 삽입 
        if(attachmentSeqs !=null && !attachmentSeqs.isEmpty()) {
        	for(Integer attachmentSeq : attachmentSeqs) {
        		jobApplicationMapper.insertApplicationAttachment(jobApplicationSeq, attachmentSeq);
        	}
        }
        
    }
    
}