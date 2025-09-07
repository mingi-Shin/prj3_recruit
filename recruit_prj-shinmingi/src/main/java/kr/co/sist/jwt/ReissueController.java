package kr.co.sist.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.sist.jwt.ReissueService.ReissueResult;
import kr.co.sist.user.dto.UserDTO;

/**
 *	리프레시 검증후 access토큰 재생성 컨트롤러 
 */
@RestController
public class ReissueController {

	private final ReissueService reService;
	
	public ReissueController(ReissueService reService) {
		this.reService = reService;
	}
	
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response){
		
		//발급은 서비스, 응답은 컨트롤 
		ReissueResult result = reService.reissueAccessToken(request, response);
		
    switch (result) {
    case NULL:
        return new ResponseEntity<>("refresh Token is null", HttpStatus.BAD_REQUEST);
    case INVALID:
        return new ResponseEntity<>("refresh Token is invalid", HttpStatus.BAD_REQUEST);
    case EXPIRED:
        return new ResponseEntity<>("refresh Token is expired", HttpStatus.BAD_REQUEST);
    case SUCCESS:
        return new ResponseEntity<>("reissuing new access token is success!", HttpStatus.OK);
    default:
        return new ResponseEntity<>("unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
	}
	
	
	
	
	
	
}
