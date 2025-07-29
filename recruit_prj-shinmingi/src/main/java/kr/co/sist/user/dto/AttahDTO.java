package kr.co.sist.user.dto;

import lombok.Data;

@Data
public class AttahDTO {

  private Integer attachmentSeq;
  private String email;
  private String originalName;
  private String fileName;
  private long fileSize;
  private String fileType;
  private String createdAt;

}
