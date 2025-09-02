package kr.co.sist.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ReissueController {

	private final JWTUtil jwtUtil;
	
	public ReissueController (JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}
	
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response){
		
		//리프레쉬 토큰 가져와
		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("refresh")) {
				refresh = cookie.getValue();
			}
		}
		
		//refresh null검증
		if(refresh == null) {
			//response status code
			return new ResponseEntity<>("refresh Token is null", HttpStatus.BAD_REQUEST);
		}
		
		//expired 체크
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			e.printStackTrace();
			return new ResponseEntity<>("refresh Token is expired", HttpStatus.BAD_REQUEST);
		}
		
		
		
		
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
}
