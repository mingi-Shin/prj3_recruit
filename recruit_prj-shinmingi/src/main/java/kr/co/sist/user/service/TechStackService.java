package kr.co.sist.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.sist.user.dto.TechStackDTO;
import kr.co.sist.user.mapper.TechStackMapper;

@Service
public class TechStackService {

	private final TechStackMapper tsm;
	
	public TechStackService(TechStackMapper tsm) {
		this.tsm = tsm;
	}
	
	//이력서 폼에서 검색한 내용을 포함하는 기술스택 리스트를 가져오기
	public List<TechStackDTO> searchInputTechStack(String stackName){
		return tsm.selectInputTechStack(stackName);
	}
}
