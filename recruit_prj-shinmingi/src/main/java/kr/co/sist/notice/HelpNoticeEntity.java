package kr.co.sist.notice;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "NOTICE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HelpNoticeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTICE_SEQ")
    private Long noticeSeq;

    @Column(name = "ADMIN_ID")
    private String adminId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENT")
    private String content;

   @Column(name = "REGS_DATE")
   private String regsDate;
   
   @Column(name = "MODIFY_DATE")
   private String modifyDate;
}