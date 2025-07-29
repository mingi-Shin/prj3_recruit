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
 *  http.formLogin().loginProcessingUrl(...)에 의해 로그인 요청이 들어오면,
 *  AuthenticationManager가 UserDetailsService.loadUserByUsername()를 자동 호출해서 사용자 정보를 로드합니다.
 *  
 *  정확히 여기서 로그인 정보를 받아서 걸러낸다. 
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
															.orElseThrow(() -> new BadCredentialsException("없는 계정.. 이메일 비번 틀렸거나 "));
		
		UserDTO userDTO = UserDTO.from(userEntity);
		
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
