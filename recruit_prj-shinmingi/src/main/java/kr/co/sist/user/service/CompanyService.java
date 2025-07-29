package kr.co.sist.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.sist.user.dto.CompanyDTO;
import kr.co.sist.user.mapper.CompanyInfoMapper;

@Service
public class CompanyService {
	
	@Autowired
	private CompanyInfoMapper companyMapper;
	
	public CompanyDTO getCompanyInfoByCorpNo(long corpNo) {
		
	  return companyMapper.selectCompanyByCorpNo(corpNo);
	}

}
