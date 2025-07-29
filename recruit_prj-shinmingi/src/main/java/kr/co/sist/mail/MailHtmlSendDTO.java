package kr.co.sist.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailHtmlSendDTO {

	//수신자 이메일 
	private String emailAddr;
	
	//이메일 제목
	private String subject;
	
	//이메일 내용
	private String content;
	
	// 이메일 대상 
	private String target;
	
}
