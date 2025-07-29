package kr.co.sist.corp.service;

import org.springframework.stereotype.Service;

import kr.co.sist.corp.dto.ResumeDetailDTO;
import kr.co.sist.corp.mapper.ResumeDetailMapper;
import kr.co.sist.util.CipherUtil;

@Service
public class ResumeDetailServiceImpl implements ResumeDetailService {

    private final ResumeDetailMapper resumeDetailMapper;
    private final CipherUtil cu;

    public ResumeDetailServiceImpl(ResumeDetailMapper resumeDetailMapper, CipherUtil cu) {
        this.resumeDetailMapper = resumeDetailMapper;
        this.cu = cu;
    }

    @Override
    public ResumeDetailDTO getResumeDetail(Long resumeSeq, Long corpNo) {
        ResumeDetailDTO dto = resumeDetailMapper.selectResumeBasic(resumeSeq);

        if (dto == null) {
            throw new RuntimeException("Resume not found for resumeSeq: " + resumeSeq);
        }

        dto.setPositions(resumeDetailMapper.selectPositionsByResume(resumeSeq));
        dto.setSkills(resumeDetailMapper.selectTechStacksByResume(resumeSeq));
        dto.setEducations(resumeDetailMapper.selectEducationsByResume(resumeSeq));
        dto.setCareers(resumeDetailMapper.selectCareersByResume(resumeSeq));
        dto.setProjects(resumeDetailMapper.selectProjectsByResume(resumeSeq));
        dto.setAdditionals(resumeDetailMapper.selectAdditionalsByResume(resumeSeq));
        dto.setIntroductions(resumeDetailMapper.selectIntroductionsByResume(resumeSeq));
        dto.setLinks(resumeDetailMapper.selectLinksByResume(resumeSeq));

        // 스크랩 여부 조회
        String isScrapped = resumeDetailMapper.selectIsScrappedByResumeAndCorp(resumeSeq, corpNo);
        dto.setIsScrapped(isScrapped != null ? isScrapped : "N");

        // 회원 전화번호 복호화
        if (dto.getMember() != null && dto.getMember().getPhone() != null) {
            String decryptedPhone = cu.decryptText(dto.getMember().getPhone());
            dto.getMember().setPhone(decryptedPhone);
        }

        return dto;
    }
}
