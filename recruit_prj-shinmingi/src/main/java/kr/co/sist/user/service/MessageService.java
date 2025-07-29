package kr.co.sist.user.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.sist.corp.dto.CorpEntity;
import kr.co.sist.user.dto.JobPostDTO;
import kr.co.sist.user.dto.MessageDTO;
import kr.co.sist.user.dto.MessageSearchDTO;
import kr.co.sist.user.dto.MessageStatisticsDTO;
import kr.co.sist.user.entity.MessageEntity;
import kr.co.sist.user.entity.UserEntity;
import kr.co.sist.user.mapper.MessageMapper;
import kr.co.sist.user.repository.MessageRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

	private final MessageRepository messageRepos;
	private final MessageMapper messageMapper;

	// 지원서 읽을 시 메시지 보내기
	public void addResumeReadNotification(UserEntity userEntity, CorpEntity corpEntity, int jobPostingSeq) {

		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		JobPostDTO jobPostDTO = messageMapper.selectPostTitle(jobPostingSeq);
		String title = "[" + corpEntity.getCorpNm() + "]에서 지원하신 이력서를 확인했습니다.";
		String content = "안녕하세요, [" + userEntity.getName() + "]님.\n\n" + corpEntity.getCorpNm() + "에서 지원하신 \""
				+ jobPostDTO.getPostingTitle() + "\" 이력서를 확인하였습니다." + "검토 후 결과를 안내해 드릴 예정이니 조금만 기다려 주세요.\n\n감사합니다.";

		// 메시지 객체 생성
		MessageEntity message = new MessageEntity();
		message.setCorpNo(corpEntity.getCorpNo());
		message.setEmail(userEntity.getEmail());
		message.setMessageTitle(title);
		message.setMessageContent(content);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		message.setCreatedAt(now.format(formatter));

		message.setIsRead("N");

		messageRepos.save(message);
	}// addResumeReadNotification

	// 합격 상태 변경 시 메시지 보내기
	public void addMessage(String email, long corpNo, String title, String content) {

		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

		MessageEntity message = new MessageEntity();
		message.setEmail(email);
		message.setCorpNo(corpNo);
		message.setMessageTitle(title);
		message.setMessageContent(content);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		message.setCreatedAt(now.format(formatter));
		message.setIsRead("N");

		messageRepos.save(message);

	}//addMessage

	//특정 유저의 모든 메일 가져오기
	public List<MessageDTO> searchMyAllMessage(String email){
		
		return messageMapper.selectMyAllMessage(email);
	
	}
	
	//특정 유저의 모든 메일 목록 가져오기(페이징 처리)
	public List<MessageDTO> searchMyMessage(MessageSearchDTO searchDTO) {

		List<MessageDTO> messageList = messageMapper.selectMyMessage(searchDTO);
		for(MessageDTO message : messageList) {
			
			//날짜 가공
			String raw = message.getCreatedAt(); // 예: "2025-07-15T23:20:14"
      String formatted = raw.replace("T", " ").substring(2, 16); // "2025-07-15 23:20"
      message.setCreatedAt(formatted);
		}
		return messageList;
	}//searchMyMessage
	
	//특정 메시지 읽음 상태 토글
	@Transactional
	public void toggleReadMessage(int messageSeq) {
		
		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		 
		//트랜잭션이 끝나면 JPA가 자동으로 Dirty Checking 후 Update
		MessageEntity messageEntity = messageRepos.findById(messageSeq).orElse(null);
		if(messageEntity.getIsRead() == null || "N".equals(messageEntity.getIsRead())) {
			messageEntity.setIsRead("Y");
			messageEntity.setReadedAt(now.toString());
		} else {
			messageEntity.setIsRead("N");
			messageEntity.setReadedAt(null);
		}
		
	}//readMessage
	
	//메시지 통게 집계
	public MessageStatisticsDTO getMessageStatistics(List<MessageDTO> messageList) {
		
		MessageStatisticsDTO statistics = new MessageStatisticsDTO();
		
		long readCnt = messageList.stream().filter(m -> "Y".equals(m.getIsRead())).count();
		long offeredCnt = messageList.stream().filter(m -> "Y".equals(m.getIsOffered())).count();
		
		statistics.setTotal(messageList.size());
		statistics.setRead((int)readCnt);
		statistics.setUnread(messageList.size() - (int)readCnt);
		statistics.setPosition((int)offeredCnt);
		
		return statistics;
	}
	
	//내 모든 메시지 개수
	public int cntMyAllMesage(String eamil) {
		
		return messageMapper.cntMyAllMessage(eamil);
	}
	
	//검색된 내 메시지 개수
	public int cntMyMessage(MessageSearchDTO searchDTO) {
		
		return messageMapper.cntMyMessage(searchDTO);
	}
	
	//messageSeq로 메시지 객체 한개 반환
	public MessageDTO searchOneMessage(int messageSeq) {
		
		return messageMapper.selectOneMessage(messageSeq);
		
	}
	
	//해당 메시지 삭제
	public void removeMessage(int messageSeq) {
		
		messageRepos.deleteById(messageSeq);
	}
}
