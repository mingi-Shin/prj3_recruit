package kr.co.sist.user.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.sist.user.dto.JobPostDTO;
import kr.co.sist.user.mapper.JobPostingMapper;

@Service
public class JobPostingService {  
    
    @Autowired
    private JobPostingMapper jpm;
    
    public List<JobPostDTO> getJobPostings(Integer jobPostingSeq) {
      List<JobPostDTO> jobList;
      
      if (jobPostingSeq == null) {
          jobList = jpm.selectAllJobPostings(); // 전체 공고 조회
      } else {
          jobList = jpm.selectJobPostingsBySeq(jobPostingSeq); // 단건 조회 (리스트 형태)
      }


      return jobList;
  }
    
    // 특정 공고 조회
    public JobPostDTO findById(Integer jobPostingSeq) {
      if (jobPostingSeq == null) {
          throw new IllegalArgumentException("jobPostingSeq는 null일 수 없습니다.");
      }

      List<JobPostDTO> results = jpm.selectJobPostingsBySeq(jobPostingSeq);
      if (results.isEmpty()) {
          throw new RuntimeException("해당 공고를 찾을 수 없습니다. jobPostingSeq: " + jobPostingSeq);
      }

      JobPostDTO job = results.get(0);


      return job;
  }
    
    
    // 조회수 증가
    public void incrementViewCount(Integer jobPostingSeq) {
        if (jobPostingSeq == null) {
            throw new IllegalArgumentException("jobPostingSeq는 null일 수 없습니다.");
        }
        
        // findById 메서드를 재사용하여 일관성 유지
        JobPostDTO jobPost = findById(jobPostingSeq);
        int newViewCount = jobPost.getViewCnt() + 1;
        jpm.updateJobPostingViewCount(jobPostingSeq, newViewCount);
    }
    
    // 랜덤 공고 조회
    public List<JobPostDTO> getRandomJobPostings() {
        List<JobPostDTO> postings = jpm.selectAllJobPostings();
        
        if (postings == null || postings.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 중복 제거 (같은 jobPostingSeq의 공고들)
        Map<Integer, JobPostDTO> uniquePostings = new LinkedHashMap<>();
        for (JobPostDTO job : postings) {
            uniquePostings.put(job.getJobPostingSeq(), job);
        }
        
        List<JobPostDTO> uniqueList = new ArrayList<>(uniquePostings.values());
        Collections.shuffle(uniqueList);
        
        return uniqueList.subList(0, Math.min(uniqueList.size(), 4));
    }
    
    // 인기순 공고 가져오기 (viewCnt 기준 내림차순)
    public List<JobPostDTO> getPopularJobPostings() {
        return jpm.getPopularJobPostings();
    }
    
    
    
    /**
     * 특정 기업의 채용공고 목록 조회 (활성화된 공고만)
     */
    public List<JobPostDTO> getJobPostsByCorpNo(long corpNo) {
        List<JobPostDTO> jobList = jpm.selectJobPostingsByCorpNo(corpNo);
        
        // 각 공고에 대해 D-day 계산 및 마감 여부 설정
        for (JobPostDTO job : jobList) {
            calculateDaysRemaining(job);
        }
        
        return jobList;
    }
    
    /**
     * 특정 기업의 활성화된 채용공고 조회 (현재 공고 제외)
     * - 공고 상세페이지에서 "이 회사의 다른 공고" 표시용
     */
    public List<JobPostDTO> getCompanyActiveJobs(long corpNo, Integer excludeJobPostingSeq) {
        Map<String, Object> params = new HashMap<>();
        params.put("corpNo", corpNo);
        params.put("excludeJobPostingSeq", excludeJobPostingSeq);
        params.put("limit", 5); // 최대 5개까지만 표시
        
        List<JobPostDTO> jobs = jpm.selectCompanyActiveJobs(params);
        
        // D-day 계산
        for (JobPostDTO job : jobs) {
            calculateDaysRemaining(job);
        }
        
        return jobs;
    }
    
    /**
     * D-day 계산 및 마감 여부 설정
     */
    private void calculateDaysRemaining(JobPostDTO job) {
        if (job.getPostingEndDt() != null && !job.getPostingEndDt().trim().isEmpty()) {
            try {
                LocalDate endDate = LocalDate.parse(job.getPostingEndDt());
                LocalDate today = LocalDate.now();
                long days = ChronoUnit.DAYS.between(today, endDate);
                
                if (days < 0) {
                    job.setIsEnded("Y");
                    job.setDaysRemaining(-1);
                } else {
                    job.setIsEnded("N");
                    job.setDaysRemaining((int) days);
                }
            } catch (Exception e) {
                // 날짜 파싱 오류 시 마감으로 처리
                job.setIsEnded("Y");
                job.setDaysRemaining(-1);
            }
        } else {
            // 마감일이 없는 경우 마감으로 처리
            job.setIsEnded("Y");
            job.setDaysRemaining(-1);
        }
    }
}

