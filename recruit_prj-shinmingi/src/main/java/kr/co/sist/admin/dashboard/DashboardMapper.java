package kr.co.sist.admin.dashboard;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.sist.admin.ask.AdminInquiryDTO;
import kr.co.sist.user.entity.InquiryEntity;

@Mapper
public interface DashboardMapper {
    List<Map<String, Object>> selectUserCnt();
    List<Map<String, Object>> selectCorpCnt();
    List<Map<String, Object>> selectIndCnt();
		List<InquiryEntity> selectAsk();
}