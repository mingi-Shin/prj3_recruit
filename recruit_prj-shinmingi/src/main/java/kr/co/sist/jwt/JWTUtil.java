package kr.co.sist.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.sist.login.LoginController;
import kr.co.sist.user.dto.UserDTO;
import kr.co.sist.user.entity.UserEntity;
import kr.co.sist.user.repository.AdditionalInfoRepository;
import kr.co.sist.util.CipherUtil;

@Component
public class JWTUtil {

	private final SecretKey secretKey;
	
	private final CipherUtil cipherUtil;
	
	private final Environment environment;
	
	//application.properties에서 시크릿 키를 주입받아
	public JWTUtil(@Value("${spring.jwt.mysecret}") String secret, CipherUtil cipherUtil, Environment environment) {
	   
	  // 문자열 형태의 시크릿 키를 바이트 배열로 변환하고,
	  // HMAC SHA-256 알고리즘을 사용하는 SecretKey 객체로 생성
	  secretKey = new SecretKeySpec(
       secret.getBytes(StandardCharsets.UTF_8),
       Jwts.SIG.HS256.key().build().getAlgorithm()
	  );
	  
	  this.cipherUtil = cipherUtil;
	  this.environment = environment;
	  
	  
	}
	
	public String getEmail(String token) { 					//JWT(token) 에서 이메일뽑기 메서드임(헤더, payload, 서명), 여기서는 아이디역할 

		try {
		    return Jwts.parser()													// 파서(parser)를 생성
		               .verifyWith(secretKey)							// 서명 검증
		               .build()														// 빌더를 완성
		               .parseSignedClaims(token)					// 서명을 검증한 뒤 JWT의 본문(Claims)을 파싱
		               .getPayload()											// 파싱된 클레임 데이터에서 페이로드 부분(실제 데이터를 담고 있는 JSON)을 반환
		               .get("email", String.class);		// JSON 클레임 중 "email"이라는 키에 해당하는 값을 추출
		} catch (JwtException e) {
			e.printStackTrace();
		    throw new IllegalArgumentException("Invalid JWT token", e);
		}	
	}
	
	public Long getCorpNo(String token) {
    try {
        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        Long corpNo = claims.get("corpNo", Long.class);
        return corpNo; // null일 수도 있음
    } catch (JwtException e) {
        e.printStackTrace();
        throw new IllegalArgumentException("Invalid JWT token", e);
    }
	}
	
	public String getRole(String token) { //토큰에서 Role 뽑기 
		
		try {
			return Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(token)
					.getPayload()
					.get("role", String.class);
		} catch (JwtException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid JWT token", e);
		}
	}
	
	public Boolean isExpired(String token) { //토큰에서 만료시간 검증
		
  	try {
		return Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(token)
					.getPayload()
					.getExpiration().before(new Date());
		} catch (JwtException e) {
			//e.printStackTrace();
			throw new IllegalArgumentException("Invalid JWT token:만료됨", e);
		}
  }
    
  public String getName(String token) { //토큰에서 이름 뽑기
  	
  	try {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("name", String.class);
		} catch (JwtException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid JWT token", e);
		}
  }
  
  public String getIss(String token) { //토큰에서 발급자 뽑기 ( 우리는 mingiRecruit)
  	
  	try {
  		return Jwts.parser()
  				.verifyWith(secretKey)
  				.build()
  				.parseSignedClaims(token)
  				.getPayload()
  				.get("iss", String.class);
  	} catch (JwtException e) {
  		e.printStackTrace();
  		throw new IllegalArgumentException("Invalid JWT token", e);
  	}
  }
  
  public String getCategory(String token) { //토큰에서 카테고리 뽑기
  	
  	try {
  		return Jwts.parser()
  				.verifyWith(secretKey)
  				.build()
  				.parseSignedClaims(token)
  				.getPayload()
  				.get("category", String.class);
  	} catch (JwtException e) {
  		e.printStackTrace();
  		throw new IllegalArgumentException("Invalid JWT token", e);
  	}
  }
  
  
	// JWT생성: 더 많은 정보를 담고싶다면 매개변수에 포함.
  // category = access, referesh 추가 (25.07.23)
	//public String createJwt(String email, String name, String role, Long corpNo, Long expiredMs, String iss) {
	public String createJwt(String category, UserDTO user ,Long expiredMs) {

		System.out.println("createJwt() 실행~ ");

		try {
			return Jwts.builder() // 빌더를 통해 JWT의 클레임(Claims), 헤더(Header), 서명(Signature)을 구성
					.claim("category", category) // access || refresh 
					.claim("email", user.getEmail())	// JWT의 Custom Claim(사용자 정의 클레임)을 추가 (키 : 값) -> // Payload 에 저장
					.claim("corpNo", user.getCorpNo())
					.claim("name", user.getName()) 
					.claim("role", user.getRole())
					.claim("iss", "mingiRecruit") //발급자 검증 
					.issuedAt(new Date(System.currentTimeMillis())) // 토큰의 발급 시간 설정
					.expiration(new Date(System.currentTimeMillis() + expiredMs)) // JWT의 만료 시간 설정
					.signWith(secretKey) // 입력된 암호화 키(secretKey)를 사용해 JWT의 서명을 생성
					.compact(); // 최종적으로 JWT를 문자열로 압축하여 반환
		} catch (Exception e) {
			// 예외 처리 (로그 기록, 재처리 등)
			e.printStackTrace();
			throw new RuntimeException("JWT 생성 실패", e); // 예외 발생 시 RuntimeException 던짐
		}
	}
	
	
	/**
	 * 요청에서 쿠키 꺼내기
	 */
	public String resolveToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("Authorization")) { // 로그인 컨트롤러에서 이 이름으로 발급중 
					return cookie.getValue(); //쿠키 이름이 token인거의 값 반환
				}
			}
		}
		return null;
	}
	
	/**
	 * 토큰 검증?? 만료시간? 등등
	 */
	public UserDTO validateToken(String token) {
	  if (token == null || token.trim().isEmpty()) {
      return null; 
	  }
		String secretKey  = environment.getProperty("spring.jwt.mysecret"); //properties의 키값 가져오기 
		try {
			SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
			Claims claims = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token) //서명된 JWT를 파싱하고 서명이 유효한지 검증, Payload는 문자열로 반환
					.getPayload();
			
			//토큰 유효한지만 확인 = return true
			//토큰에서 사용자 정보 추출 = return claims.getSubject(); //사용자명 반환
			//토큰에서 특정 클레임 값 추출 = return claims.get(claimName, String.class);
			//토큰의 모든 클레임 정보 추출 = return claims;
			
			//토큰에서 사용자 정보를 담은 객체를 반환
			UserDTO uDTO = new UserDTO();
			uDTO.setEmail(claims.get("email", String.class));
			uDTO.setCorpNo(claims.get("corpNo", Long.class));
			uDTO.setRole(claims.get("role", String.class));
			
			//유효성 검증
	    String issuer = claims.getIssuer(); //= claims.get("iss", String.class)
	    Date expiration = claims.getExpiration(); //= claims.get("exp", Date.class)
			
	    if("mingiRecruit".equals(issuer) && expiration.after(new Date())) {
	      return uDTO;
	    } else {
	      //만료됐거나 다른 토큰 발급자
	      return null;
	    }
			  
		} catch (Exception e) {
		  e.printStackTrace();
		  return null;
		}
		
	}
	
}
/**
 * 
 * 	📌 정리
	•	JWT는 기본적으로 stateless 하므로, 프론트에서 사용자 정보를 표시하려면 DB 조회가 필요함.
	•	하지만 DB 조회가 많아지면 서버에 부담이 되므로, 일부 정보를 JWT에 포함할 수도 있음.
	•	그러나 닉네임 같은 자주 변경되는 정보는 JWT에 넣으면 불편 (변경될 때마다 토큰 재발급 필요).
	•	따라서 JWT는 인증(Authentication)과 권한(Authorization) 정보만 최소한으로 포함하는 것이 가장 좋음.
	•	그 외의 사용자 정보는 DB에서 조회하는 것이 바람직함.
	🚀 결론: JWT는 인증과 권한 용도로만 사용하고, 자주 바뀌는 데이터는 DB에서 조회하는 것이 좋다!
 */