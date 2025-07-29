package kr.co.sist.corp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MessageDTO {
    private Long corpNo;
    private String email;
    private String messageTitle;
    private String messageContent;
    private String createdAt;
    private String isRead;
    private String readedAt;
    private String isOffered; 
}
