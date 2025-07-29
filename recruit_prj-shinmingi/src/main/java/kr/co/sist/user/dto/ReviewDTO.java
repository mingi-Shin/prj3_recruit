package kr.co.sist.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewDTO {
	
	  private Integer reviewSeq;      // 후기 번호 (PK)
	  private Long corpNo;         // 기업 번호 (FK)
	  private String email;        // 작성자 이메일 (FK)
	  private int rating;          // 별점
	  private String summary;      // 한줄평
	  private String pros;         // 장점
	  private String cons;         // 단점
	  private String createdAt;    // 작성일

	  
    private double avgRating;    // 평균 평점
    private int totalReviews;    // 전체 리뷰 수
    private String corpNm;  // 회사명
    
    
}
