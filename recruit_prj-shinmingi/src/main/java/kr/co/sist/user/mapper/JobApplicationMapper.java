package kr.co.sist.user.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.sist.user.dto.AttahDTO;
import kr.co.sist.user.dto.JobApplicationDTO;
import kr.co.sist.user.dto.ResumeDTO;

@Mapper
public interface JobApplicationMapper {

	 // 1. 이메일로 이력서 조회
  List<ResumeDTO> selectResumesByEmail(@Param("email") String email); 

  // 2. (기존 방식) 공고 지원 등록 (Map 기반)
  void insertJobApplication(Map<String, Object> paramMap); 

  // 3. (DTO 방식) 공고 지원 등록
  void insertJobApplicationVO(JobApplicationDTO applicationDTO); 
  
  //4. 중복 방지  
  JobApplicationDTO selectExistingApplication(@Param("resumeSeq") Integer resumeSeq,
      @Param("jobPostingSeq") Integer jobPostingSeq);
  
  List<AttahDTO> selectAttachmentsByEmail(@Param("email") String email);
  void insertApplicationAttachment(@Param("jobApplicationSeq") Integer jobApplicationSeq, 
                                 @Param("attachmentSeq") Integer attachmentSeq);

  
  
}     
