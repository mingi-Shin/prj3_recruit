package kr.co.sist.globalController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.sist.globalController.Exceptions.LoginException;
import kr.co.sist.globalController.Exceptions.NotFoundException;
import kr.co.sist.jwt.CustomUser;
/**
 * 이 클래스에 예외처리가 안오는 이유 !
 * 1. 예외가 @RestController or @ResponseBody가 붙은 컨트롤러에서 발생한 경우 (여기도 붙여줘야 한다).
 * 2. 예외가 비동기 요청(AJAX) 중 발생한 경우
 * 3. Controller 계층에서 발생한 예외만 처리 가능! 
 * 4. 예외가 이미 try-catch로 감싸져 있는 경우 -> throw e 로 다시 던져줘야 함  
 */
@ControllerAdvice
public class GlobalHandler {
	
	/**
	 * controller 에서 ( @ModelAttribute("user") CustomUser user) 혹은 view에서 ${user}로 호출 
	 * 	vs
	 * 만약 @ModelAttribute("user")	 선언을 안한다면,
	 * controller 에서'만' ( @AuthenticationPrincipal CustomUser user )로 가져올수도 있음  -> SecurityContextHolder.getContext().getAuthentication().getPrincipal()을 자동으로 꺼내서 바인딩
	 */
	@ModelAttribute("user")	 
	public CustomUser getLoginUser(@AuthenticationPrincipal CustomUser customUser) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUser) {
			return (CustomUser) auth.getPrincipal();
		}
		
		return null;
	}
	
	@ExceptionHandler(IllegalStateException.class)
  public String handleIllegalState(IllegalStateException ex, RedirectAttributes rttr) {
    rttr.addFlashAttribute("errorMessage", ex.getMessage()); //컨트롤러에서 ex.getMessage() 내용을 정해
    return "redirect:/corp/join"; 
	}
	
	//@ExceptionHandler(IllegalArgumentException.class)
	public String handleIllegalArgument(IllegalArgumentException ex, RedirectAttributes rttr) {
		rttr.addFlashAttribute("errorMessage", ex.getMessage());
		return "redirect:/login";
	}
	
	//로그인 커스텀 예외처리 
	@ExceptionHandler(LoginException.class)
	public String handleLoginException(LoginException le, RedirectAttributes rttr) {
		rttr.addFlashAttribute("errorMessage", "아이디 또는 비밀번호가 일치하지 않습니다.");
		return "redirect:/login";
	}
	
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {
      Map<String, Object> errorBody = new LinkedHashMap<>();
      errorBody.put("timestamp", LocalDateTime.now());
      errorBody.put("status", HttpStatus.NOT_FOUND.value());
      errorBody.put("error", "Not Found");
      errorBody.put("message", ex.getMessage());

      //이거 아직 사용자쪽에 뿌리는거 안만들었어.
      return new ResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
  }	
  
	//TooManyRequestsException.java
	
	//EmailSendException.java
	
	
}
