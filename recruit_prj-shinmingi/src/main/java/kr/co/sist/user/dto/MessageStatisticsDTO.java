package kr.co.sist.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MessageStatisticsDTO {

	private int total = 0;
	private int read = 0;
	private int unread = 0;
	private int position = 0;
}
