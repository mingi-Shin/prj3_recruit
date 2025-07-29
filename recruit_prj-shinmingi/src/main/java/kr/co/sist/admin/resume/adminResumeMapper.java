package kr.co.sist.admin.resume;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.sist.user.dto.ResumeDTO;

@Mapper
public interface adminResumeMapper {
    List<ResumeDTO> selectResume();
    List<Map<String, Object>> selectResume2();
    List<Map<String, Object>> searchResume(Integer resume_seq,String name);
}