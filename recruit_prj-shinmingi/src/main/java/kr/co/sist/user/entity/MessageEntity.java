package kr.co.sist.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity(name = "MESSAGE")
public class MessageEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int messageSeq;
	
	private long corpNo;
	private String email;
	private String messageTitle;
	private String messageContent;
	private String createdAt;
	private String isOffered;
	private String isRead;
	private String readedAt;
	
}
