package kr.co.sist.user.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("userFavoriteCompanyDTO")
public class FavoriteCompanyDTO {

	private int favoriteCompanySeq;
	private long corpNo;
	private String corpNm;
	private String corpLogo;
	
	
}
