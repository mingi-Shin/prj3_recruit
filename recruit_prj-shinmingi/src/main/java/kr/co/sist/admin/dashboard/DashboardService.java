package kr.co.sist.admin.dashboard;

import java.util.List;
import java.util.Map;

import kr.co.sist.admin.ask.AdminInquiryDTO;
import kr.co.sist.user.entity.InquiryEntity;

public interface DashboardService {
    List<Map<String, Object>> getUserCountByDate();
    List<Map<String, Object>> getCorpCountByResume();
    List<Map<String, Object>> getCorpCountByIndustry();
    List<InquiryEntity> getAsk();
}