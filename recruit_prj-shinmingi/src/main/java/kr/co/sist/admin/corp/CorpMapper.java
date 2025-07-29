package kr.co.sist.admin.corp;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.sist.corp.dto.CorpEntity;
import kr.co.sist.user.dto.UserDTO;

@Mapper
public interface CorpMapper {
	List<CorpEntity> selectCorp();
	List<CorpEntity> searchCorp(String corpNo, String name, List<String> industry);
	List<String> selectCorpInd();
	CorpEntity detailCorp(String corpNo);
}