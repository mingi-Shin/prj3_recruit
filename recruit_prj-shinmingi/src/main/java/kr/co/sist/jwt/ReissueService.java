package kr.co.sist.jwt;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.sist.user.dto.UserDTO;

@Service
public class ReissueService {

	private final JWTUtil jwtUtil;
	
	public ReissueService(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}
	
	public ReissueResult reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
		
		String refresh = null;
		
		//리프레쉬 토큰 가져와서
		Cookie[] cookies = request.getCookies();

    if(cookies == null) {
    	return ReissueResult.NULL;
    }
    
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("refresh")) { // <-- cookies가 null이면 NPE 발생
				refresh = cookie.getValue();
			}
		}
		
		//null 검증하고
		if(refresh == null) {
			//response status code
			return ReissueResult.NULL;
		}
		
		//category 검증하고 
		if(!jwtUtil.getCategory(refresh).equals("refresh")) {
			return ReissueResult.INVALID;
		}
		
		//만료여부 검증하고 
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			e.printStackTrace();
			return ReissueResult.EXPIRED;
		}
		
		//다 통과했으면 새로운 access 생성 
		UserDTO uDTO = createUserDto(refresh);
		String newAccessToken = jwtUtil.createJwt("access", uDTO, 60 * 10 * 1000L);
		response.setHeader("Authorization", "Bearer " + newAccessToken);
		
		//새로운 refresh 발급으로 보안강화
		String newRefreshToken = jwtUtil.createJwt("refresh", uDTO, 24 * 60 * 60 * 1000L); //12시간 
		ResponseCookie refreshCookie = createCookie(newRefreshToken);
		response.setHeader("Set-Cookie", refreshCookie.toString());

		return ReissueResult.SUCCESS;
		
	}
	
	/**
	 * refreshToken 사용해서 UserDTO 객체만듬. 
	 * 왜? access토큰 만들때 필요하거든 
	 * DB에서 가져오는건 쓸데없자나. 
	 */
	private UserDTO createUserDto(String refreshToken) {
		UserDTO uDTO = new UserDTO();
		uDTO.setEmail(jwtUtil.getEmail(refreshToken));
		uDTO.setName(jwtUtil.getName(refreshToken));
		uDTO.setCorpNo(jwtUtil.getCorpNo(refreshToken));
		uDTO.setRole(jwtUtil.getRole(refreshToken));
		
		return uDTO;
	}
	
	/**
	 * 문자열 리턴은 오타나 유지보수시 불편, enum이나 Result객체로 관리하는게 안정적 
	 * 널, 무효값, 기간지남, 성공 
	 */
	public enum ReissueResult {
		NULL, INVALID, EXPIRED, SUCCESS
	}
	
	//쿠키생성 메서드 (ResponseCookie) : 스프링부트의 쿠키 생성클래스
	private ResponseCookie createCookie(String refreshJwt) { 
		
		ResponseCookie cookie = ResponseCookie.from("refresh", refreshJwt)  
														.httpOnly(true) //js접근불가 ('document.cookie' 불가하게됨) -> xss공격을 방어 
														.secure(false) //HTTPS에서만 동작 (개발시 false)
														.sameSite("Strict") //CSRF방지, or Lax (Strict는 너무 제한적일 수도..) 
														.path("/") //전체경로에 대해 쿠키 전송 
														.maxAge(Duration.ofHours(24)) //쿠키 24시간 유지 (JWT보다 길어야 겠지 )
														.build();
		return cookie;
	}
	/**
	 * 만약 new 생성자였다면, new ResponseCookie("refresh", refreshJwt, true, false, "Strict", "/", 24시간....);
	 * 이렇게 길고 가독성 떨어지는 코드가 되었을 것
	 * 즉, new 대신 빌더객체를 쓰는 이유는, 가독성이 좋고 필요한 속성만 선택적으로 셋팅할수 있기 때문  
	 */
	
	
}
