package kr.co.sist.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.sist.user.dto.CorpScrapDTO;

@Mapper
public interface CorpScrapMapper {
    
    // 스크랩 존재 여부 확인
    int countScrap(@Param("corpNo") long corpNo, @Param("email") String email);
    
    // 스크랩 추가
    int insertScrap(CorpScrapDTO corpScrapDTO);
    
    // 스크랩 삭제
    int deleteScrap(@Param("corpNo") long corpNo, @Param("email") String email);
}