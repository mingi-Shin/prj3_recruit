package kr.co.sist.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.sist.user.dto.AdditionalInfoDTO;
import kr.co.sist.user.dto.AttachmentDTO;
import kr.co.sist.user.dto.CareerDTO;
import kr.co.sist.user.dto.EducationHistoryDTO;
import kr.co.sist.user.dto.LinkDTO;
import kr.co.sist.user.dto.ResumeDTO;
import kr.co.sist.user.dto.ResumePositionCodeDTO;
import kr.co.sist.user.dto.ResumeTechStackDTO;
import kr.co.sist.user.dto.SelfIntroductionDTO;

@Mapper
public interface ResumeMapper {

	//유저의 모든 이력서 가져오기
	public List<ResumeDTO> selectAllResumeByUser(String email);
	
	//해당 이력서 삭제
	public void deleteResume(int resumeSeq);
	
	//해당 유저의 모든 첨부파일 가져오기
	public List<AttachmentDTO> selectAllAttachment(String email);
	
	//이력서 번호로 각 데이터 가져오기
	public List<ResumePositionCodeDTO> selectPositionByResume(int resumeSeq);
	public List<ResumeTechStackDTO> selectStackByResume(int resumeSeq);
	public LinkDTO selectLinkByResume(int resumeSeq);
	public List<EducationHistoryDTO> selectEduByResume(int resumeSeq);
	public List<CareerDTO> selectCareerByResume(int resumeSeq);
	public List<AdditionalInfoDTO> selectEtcByResume(int resumeSeq);
	public List<SelfIntroductionDTO> selectIntroByResume(int resumeSeq);
	
	//이력서 번호로 데이터 삭제
	public void deletePositionByResume(int resumeSeq);
	public void deleteStackByResume(int resumeSeq);
	public void deleteLinkByResume(int resumeSeq);
	public void deleteEducationByResume(int resumeSeq);
	public void deleteCareerByResume(int resumeSeq);
	public void deleteProjectByResume(int resumeSeq);
	public void deleteIntroByResume(int resumeSeq);
	public void deleteAdditionalByResume(int resumeSeq);
}
