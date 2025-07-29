package kr.co.sist.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.sist.user.dto.TechStackDTO;

@Mapper
public interface TechStackMapper {

	//이력서 폼에서 검색한 내용을 포함하는 기술스택 리스트를 가져오기
	public List<TechStackDTO> selectInputTechStack(String stackName);
}
