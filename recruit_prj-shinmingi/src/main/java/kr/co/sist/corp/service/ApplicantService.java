package kr.co.sist.corp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.sist.corp.dto.ApplicantDTO;
import kr.co.sist.corp.dto.ApplicantSearchDTO;
import kr.co.sist.corp.dto.ApplicationAttachmentDTO;
import kr.co.sist.corp.dto.JobPostingDTO;
import kr.co.sist.corp.dto.ResumeScrapDTO;
import kr.co.sist.corp.mapper.ApplicantMapper;
import kr.co.sist.corp.mapper.TalentPoolMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicantService {

	private final ApplicantMapper applicantMapper;
	private final TalentPoolMapper talentPoolMapper;

	// 해당 회사의 모든 지원자 찾기
	public List<ApplicantDTO> searchAllApplicant(ApplicantSearchDTO searchDTO) {

		List<ApplicantDTO> applicantDTO = applicantMapper.selectAllApplicant(searchDTO);

		return manufactureApplicantDTO(applicantDTO);
	}// searchAllApplicant

	// 조건에 맞는 지원자 찾기
	public List<ApplicantDTO> searchApplicant(ApplicantSearchDTO searchDTO) {

		List<ApplicantDTO> applicantDTO = applicantMapper.selectApplicant(searchDTO);

		return manufactureApplicantDTO(applicantDTO);
	}
	
	//조건에 맞는 지원자 모두 찾기(엑셀영)
	public List<ApplicantDTO> searchApplicantForExcel(ApplicantSearchDTO searchDTO) {
		
		List<ApplicantDTO> applicantDTO = applicantMapper.selectApplicantForExcel(searchDTO);

		return manufactureApplicantDTO(applicantDTO);
	}

	// 모든 진행중인 공고
	public List<JobPostingDTO> searchPostingProgress(long corpNo) {

		return applicantMapper.selectPostingProgress(corpNo);

	}// searchPostingProgress

	// 모든 마감된 공고
	public List<JobPostingDTO> searchPostingClosed(long corpNo) {

		return applicantMapper.selectPostingClosed(corpNo);

	}// searchPostingClosed

	// 모든 공고
	public List<JobPostingDTO> searchPostingAll(long corpNo) {

		return applicantMapper.selectPostingAll(corpNo);

	}// searchPostingClosed

	// applicantDTO 가공해서 반환
	public List<ApplicantDTO> manufactureApplicantDTO(List<ApplicantDTO> applicantDTO) {

		for (ApplicantDTO applicant : applicantDTO) {

			// 경력 설정
			applicant.setCareerType((applicant.getCareerType().equals("N")) ? "신입" : "경력");
			// 합격 상태 설정
			String passStage = switch (applicant.getPassStage()) {
			case 0 -> "신규지원";
			case 1 -> "서류통과";
			case 2 -> "최종합격";
			case 3 -> "불합격";
			default -> "미정";
			};
			applicant.setStageName(passStage);

			// 지원 상태 설정
			String applicationStatus = switch (applicant.getApplicationStatus()) {
			case 0 -> "미열람";
			case 1 -> "열람";
			case 2 -> "지원취소";
			default -> "미정";
			};
			applicant.setStatusName(applicationStatus);

		}

		return applicantDTO;

	}// manufactureApplicantDTO

	// 지원서 읽음 처리
	public int modifyResumeReadStatus(int resumeSeq, int jobPostingSeq) {

		ApplicantDTO applicationDTO = new ApplicantDTO();
		applicationDTO.setResumeSeq(resumeSeq);
		applicationDTO.setJobPostingSeq(jobPostingSeq);
		applicationDTO = applicantMapper.selectOneApplicant(applicationDTO);

		return applicantMapper.updateResumeReadStatus(applicationDTO);

	}// modifyResumeReadStatus

	// 북마크 추가
	public void addBoomark(long resumeSeq, long corpNo) {

		// 이미 스크랩 되어있으면 return
		if (talentPoolMapper.isResumeScrapped(resumeSeq, corpNo) >= 1)
			return;

		ResumeScrapDTO scrapDTO = null;

		if (talentPoolMapper.checkScrapExists(resumeSeq, corpNo) >= 1) { // 이미 한번 생성된 상태면 update

			scrapDTO = talentPoolMapper.selectScrap(resumeSeq, corpNo);
			talentPoolMapper.updateScrap(scrapDTO);

		} else { // 아니면 insert

			scrapDTO = new ResumeScrapDTO();
			scrapDTO.setResumeSeq(resumeSeq);
			scrapDTO.setCorpNo(corpNo);
			talentPoolMapper.insertScrap(scrapDTO);
		}

	}// addBoomark

	// 북마크 제거
	public void removeBookmark(long resumeSeq, long corpNo) {

		if (talentPoolMapper.checkScrapExists(resumeSeq, corpNo) == 0)
			return;

		ResumeScrapDTO scrapDTO = talentPoolMapper.selectScrap(resumeSeq, corpNo);
		talentPoolMapper.updateScrapN(scrapDTO);

	}// removeBookmark

	// 스크랩 여부 가져오기
	public boolean isScraped(long resumeSeq, long corpNo) {

		if (talentPoolMapper.isResumeScrapped(resumeSeq, corpNo) >= 1) {
			return true;
		}

		return false;

	}// isScraped

	// 합격 상태 변경하기
	public void modifyPassStage(int resumeSeq, int jobPostingSeq, int passStage) {

		ApplicantDTO applicantDTO = new ApplicantDTO();

		//해당 조건으로 유일한 지원서 찾기
		applicantDTO.setResumeSeq(resumeSeq);
		applicantDTO.setJobPostingSeq(jobPostingSeq);
		applicantDTO = applicantMapper.selectOneApplicant(applicantDTO);

		//찾은 지원서의 합격 상태 변경
		applicantDTO.setPassStage(passStage);

		applicantMapper.updateResumePassStage(applicantDTO);

	}// modifyPassStage
	
	//해당 회사의 모든 지원자 수 가져오기
	public int searchAllApplicantCnt(long corpNo) {
		
		return applicantMapper.selectAllApplicantCnt(corpNo);

	}
	
	//검색 조건에 맞는 지원자 수 가져오기
	public int searchApplicantCnt(ApplicantSearchDTO searchDTO) {
		
		return applicantMapper.selectApplicantCnt(searchDTO);
	}
	
	//해당 지원서의 첨부파일 가져오기
	public List<ApplicationAttachmentDTO> searchApplicantAttachment(int jobApplicationSeq){
		
		return applicantMapper.selectApplicantAttachment(jobApplicationSeq);
	}

}// class
