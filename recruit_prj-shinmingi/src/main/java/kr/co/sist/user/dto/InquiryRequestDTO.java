package kr.co.sist.user.dto;

import kr.co.sist.jwt.CustomUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class InquiryRequestDTO {
    private String email;
    private String adminId;
    private String title;
    private String content;
    private String category;
    private String answerStat;
    private String userType;
    public InquiryRequestDTO() {
		}
}