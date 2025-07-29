package kr.co.sist.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.sist.user.dto.JobPostDTO;
import kr.co.sist.user.dto.MessageDTO;
import kr.co.sist.user.dto.MessageSearchDTO;

@Mapper
public interface MessageMapper {
	
	public JobPostDTO selectPostTitle(int jobPostingSeq);
	
	public List<MessageDTO> selectMyAllMessage(String email);
	public List<MessageDTO> selectMyMessage(MessageSearchDTO searchDTO);
	
	public int cntMyAllMessage(String email);
	public int cntMyMessage(MessageSearchDTO searchDTO);
	
	public MessageDTO selectOneMessage(int messageSeq);
	
}
