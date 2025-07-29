package kr.co.sist.admin.dashboard;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.sist.admin.ask.AdminInquiryDTO;
import kr.co.sist.user.entity.InquiryEntity;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public List<Map<String, Object>> getUserCountByDate() {
        return dashboardMapper.selectUserCnt();
    }
    
    @Override
    public List<Map<String, Object>> getCorpCountByResume() {
    	return dashboardMapper.selectCorpCnt();
    }
    
    @Override
    public List<Map<String, Object>> getCorpCountByIndustry() {
    	return dashboardMapper.selectIndCnt();
    }
    @Override
    public List<InquiryEntity> getAsk() {
    	return dashboardMapper.selectAsk();
    }
}