package kr.co.sist.corp.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("corpApplicationAttachmentDTO")
public class ApplicationAttachmentDTO {

	private int attachmentSeq;
	private String originalName;
	private String fileName;
	private String fileType;
}
