package kr.co.sist.login;


import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kr.co.sist.jwt.CustomUser;
import kr.co.sist.user.dto.UserDTO;
import kr.co.sist.user.entity.UserEntity;

/**
 *  
http.formLogin().loginProcessingUrl(...)에 의해 로그인 요청이 들어오면,

1. UsernamePasswordAuthenticationFilter가 요청을 가로채고,
   내부적으로 AuthenticationManager.authenticate()를 호출합니다.

2. AuthenticationManager는 등록된 AuthenticationProvider(기본: DaoAuthenticationProvider)를 사용해
   UserDetailsService.loadUserByUsername()를 자동 호출하여 사용자 정보를 로드하고
   PasswordEncoder로 비밀번호를 검증합니다.

3. 인증이 성공하면 AuthenticationSuccessHandler로,
   예외가 발생하면 AuthenticationFailureHandler로 흐름이 넘어갑니다. 
   */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;
	
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository; 
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		UserEntity userEntity = userRepository.findById(email)
															.orElseThrow(() -> new BadCredentialsException("개발자에게 보여짐 : 존재하지 않는 계정으로 접속"));
		//위의 BadCredentialsException 예외는 AuthenticationFailureHandler에 넘어갈때, exception.getCause() 값이 BadCredentialsException이다.
		// 이메일은 존재하는데, 비밀번호가 다를 때 뱉는 BadCredentialsException는 exception 자체 값이 되어있고.. 뭔차이냐?
		
		UserDTO userDTO = UserDTO.from(userEntity);
		
		//예외 발생시 AuthenticationFailureHandler 로 넘어감 
		//운영자에 의해 제재당하였을 때. ( ActiveStatus = 1 )
		if(userDTO.getActiveStatus() == 1) {
			throw new DisabledException("운영수칙을 위반하여 제재된 계정");
		}
		//탈퇴된 구직 회원이 재로그인 시도할 때 ( ActiveStatus = 2 )
		if(userDTO.getActiveStatus() == 2) {
			throw new AccountExpiredException("회원 탈퇴한 계정");
		}
		//탈퇴된 기업 회원이 재로그인 시도할 때 ( ActiveStatus = 3 )
		if(userDTO.getActiveStatus() == 3) {
			throw new AccountExpiredException("탈퇴한 관리인 계정");
		}
		
		return new CustomUser(userDTO); //CustomUser를 Entity로 만들면 형변환은 필요없지만..
	}

}
/**
 * Spring Security는 내부적으로 UsernameNotFoundException을 가로채서 BadCredentialsException으로 바꿔버리기 때문입니다
 * 이것은 보안상 이유로 "아이디가 틀렸는지", "비밀번호가 틀렸는지"를 구분하지 않기 위해 의도적으로 같은 에러로 덮는 것입니다.
 * 
 */
