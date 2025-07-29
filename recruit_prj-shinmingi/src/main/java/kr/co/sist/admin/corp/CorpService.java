package kr.co.sist.admin.corp;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.sist.corp.dto.CorpEntity;

@Service
public class CorpService {
	@Autowired
	private CorpAdminRepository cr;
	@Autowired
	private CorpMapper cm;
	
	public List<CorpEntity> searchAll() {
		return cr.findAll();
	}
	
	public List<CorpEntity> searchNameMember(String name) {
		return cr.findByCorpNmContaining(name);
	}
	
	public List<CorpEntity> searchNoMember(String num) {
		return cr.findByCorpNo(Long.parseLong(num));
	}
	
	public List<CorpEntity> selectCorp() {
		return cm.selectCorp();
	}
	public List<String> selectCorpInd() {
		return cm.selectCorpInd();
	}
	
	public List<CorpEntity> searchCorp(String corpNo, String name, List<String> industry){
		return cm.searchCorp(corpNo, name, industry);
	}
	
	public CorpEntity detailCorp(String corpNo) {
		return cm.detailCorp(corpNo);
	}

}
