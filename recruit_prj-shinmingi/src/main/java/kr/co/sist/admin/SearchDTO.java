package kr.co.sist.admin;



public class SearchDTO {

  private String keyword;         // 검색어
  private String type;            // 검색 타입 (all, author, userId)
  private String sortField;   // 정렬 대상 필드, 기본값 id
  private String sortOrder = "desc"; // 정렬 순서, 기본값 desc
  private int page = 0;           // 요청 페이지 번호 (0부터 시작)
  private int size = 10;          // 페이지 크기
  private int offset;
  private String rating;     // 평점 필터 추가


  public String getRating() {
      return rating;
  }

  public void setRating(String rating) {
      this.rating = rating;
  }
  /**
   * 기본키 컬럼명은 달라지니까 혹시나 또 쓸 일 있을까봐 생성자로 값 받기
   * @param sortField
   */
  public SearchDTO(String sortField) {
  	this.sortField = sortField;
  }
	
  // 모든 getter, setter 작성 (생략하지 말고 꼭 만드세요)
  public String getKeyword() {
      return keyword;
  }

  public void setKeyword(String keyword) {
      this.keyword = keyword;
  }

  public String getType() {
      return type;
  }

  public void setType(String type) {
      this.type = type;
  }

  public String getSortField() {
      return sortField;
  }

  public void setSortField(String sortField) {
      this.sortField = sortField;
  }

  public String getSortOrder() {
      return sortOrder;
  }

  public void setSortOrder(String sortOrder) {
      this.sortOrder = sortOrder;
  }

  public int getPage() {
      return page;
  }

  public void setPage(int page) {
      if(page < 0) this.page = 0;
      else this.page = page;
  }

  public int getSize() {
      return size;
  }

  public void setSize(int size) {
      if(size <= 0) this.size = 10;
      else this.size = size;
  }
  
  // offset getter/setter 추가
  public int getOffset() {
      return offset;
  }

  public void setOffset(int offset) {
      this.offset = offset;
  }
}
