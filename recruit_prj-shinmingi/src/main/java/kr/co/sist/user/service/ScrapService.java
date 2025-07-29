package kr.co.sist.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.sist.user.dto.ScrapDTO;
import kr.co.sist.user.mapper.ScrapMapper;

@Service
public class ScrapService {

    @Autowired
    private ScrapMapper scrapMapper;
    
    // 이미 스크랩했는지 체크
    public boolean isAlreadyScrapped(Integer jobPostingSeq, String email) {
        return scrapMapper.countScrap(jobPostingSeq, email) > 0;
    }

    // 스크랩 추가
    public void addScrap(ScrapDTO scrapDTO) {
        scrapMapper.insertScrap(scrapDTO);
    }

    // 스크랩 삭제
    public boolean removeScrap(Integer jobPostingSeq, String email) {
        return scrapMapper.deleteScrap(jobPostingSeq, email) > 0;
    }
}

