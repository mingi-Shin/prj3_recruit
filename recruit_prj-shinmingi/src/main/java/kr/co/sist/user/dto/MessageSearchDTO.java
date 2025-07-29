package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userMessageSearchDTO")
public class MessageSearchDTO {
	
	private String email;
	private String type;
	
	// 검색 관련
	private String keyword;

	// 정렬 관련
	private String sortBy;

	// 페이징 관련
	private int size = 10;
	private int page = 1;

	// 페이지 버튼 관련
	private int totalCnt;
	private int searchCnt;
	private int startPage;
	private int endPage;
	private int totalPage;

	private boolean prev;
	private boolean next;
	private int pageGroup = 5;

	// 검색 데이터 수 설정시 계산 로직
	public void setSearchCnt(int searchCnt) {
		this.searchCnt = searchCnt;
		calcPage();
	}

	// 시작 행 계산
	public int getOffSet() {
		return (page - 1) * size;
	}

	// 페이지 관련 계산
	public void calcPage() {

		// 총 페이지 수
		totalPage = (int) Math.ceil(searchCnt / (double) size);

		// 현재 페이지 그룹의 시작 페이지 계산
		startPage = ((page - 1) / pageGroup) * pageGroup + 1;

		// 현재 페이지 그룹의 끝 페이지 계산
		endPage = startPage + pageGroup - 1;
		if (endPage > totalPage) {
			endPage = totalPage;
		}

		// 이전, 다음 버튼 표시 여부
		prev = startPage > 1;
		next = endPage < totalPage;
	}

}
