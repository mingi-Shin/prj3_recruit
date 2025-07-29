package kr.co.sist.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.sist.user.dto.ScrapDTO;

@Mapper
public interface ScrapMapper {

    // 스크랩 존재 여부 확인
    int countScrap(@Param("jobPostingSeq") Integer jobPostingSeq, @Param("email") String email);

    // 스크랩 추가
    int insertScrap(ScrapDTO scrapDTO);

    // 스크랩 삭제
    int deleteScrap(@Param("jobPostingSeq") Integer jobPostingSeq, @Param("email") String email);
}