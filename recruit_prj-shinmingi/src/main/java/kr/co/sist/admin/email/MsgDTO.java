package kr.co.sist.admin.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MsgDTO {
	private String msg;
	private boolean flag;
	
	public MsgDTO(String msg) {
		this.msg = msg;
	}
}
