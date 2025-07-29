package kr.co.sist.corp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO {
	
	private Integer tagSeq;
	private String tagType;
	private String tagDetail;
	private String isActive;
}
