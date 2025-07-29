package kr.co.sist.admin.notice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Notice {
 private int noticeSeq;
 private String adminId;
 private String title;
 private String content;
 private String regsDate;

 // Getter, Setter, Constructor 등 필요한 메서드 추가

 


}
