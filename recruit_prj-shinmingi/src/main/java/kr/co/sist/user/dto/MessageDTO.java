package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userMessageDTO")
public class MessageDTO {

	private int messageSeq;
	private long corpNo;
	private String email;
	private String messageTitle;
	private String messageContent;
	private String createdAt;
	private String isOffered;
	private String isRead;
	private String readedAt;

	private String corpNm; // company

}
