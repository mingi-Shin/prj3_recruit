package kr.co.sist.user.dto;

import lombok.Data;

@Data
public class ReviewSearchDTO {
    
    private Long corpNo;
    private int page = 1;
    private int size = 5;  // 한 페이지에 3개씩
    
    // 페이징 관련 추가 필드
    private int totalPages;      // 전체 페이지 수
    private long totalElements;  // 전체 리뷰 수
    private int startPage;       // 시작 페이지 번호
    private int endPage;         // 끝 페이지 번호
    private boolean hasNext;     // 다음 페이지 존재 여부
    private boolean hasPrevious; // 이전 페이지 존재 여부
    
    public int getOffset(){
        return (page - 1) * size;
    }
    
    // 페이징 정보 계산 메서드
    public void calculatePageInfo(long totalElements) {
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        
        // 페이지 그룹 계산 (한 번에 보여줄 페이지 번호 개수: 5개)
        int pageGroupSize = 5;
        this.startPage = ((page - 1) / pageGroupSize) * pageGroupSize + 1;
        this.endPage = Math.min(startPage + pageGroupSize - 1, totalPages);
        
        // 이전/다음 페이지 존재 여부
        this.hasPrevious = page > 1;
        this.hasNext = page < totalPages;
    }
}
