package kr.co.sist.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.sist.user.dto.CompanyDTO;

@Mapper
public interface CompanyInfoMapper {
	
  CompanyDTO selectCompanyByCorpNo(@Param("corpNo") long corpNo);

}
