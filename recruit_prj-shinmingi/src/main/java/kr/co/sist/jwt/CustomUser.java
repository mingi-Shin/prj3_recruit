package kr.co.sist.jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import kr.co.sist.corp.dto.CorpEntity;
import kr.co.sist.user.dto.UserDTO;
import kr.co.sist.user.entity.UserEntity;
import lombok.Data;

/**
 * 	(CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
 * 	로 꺼내올수 있게된다. 
 *	꺼내서 쓸 값들을 여기서 get메서드로 만들어주면 된다.
 */
@Data
public class CustomUser implements UserDetails{
	
	private UserDTO userDTO;
	
	public CustomUser(UserDTO userDTO) {
		this.userDTO = userDTO;
	}
	
	//권한이 여러개인 경우가 보통 
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(userDTO.getRole()));
		
		return authorities;
	}

	//스프링 시큐리티가 PasswordEncoder.matches() 자동 호출하여 비교 
	@Override
	public String getPassword() {
		return userDTO.getPassword();
	}

	@Override //이름이 아니라 아이디입니다. 헷갈리시면 안됨 
	public String getUsername() {
		return userDTO.getEmail();
	}
	
	//헷갈리니까 getEmail도 추가하자
	public String getEmail() {
		return userDTO.getEmail();
	}
	
	//기업회원은 사업자등록번호 반환 
	public Long getCorpNo() {
		if(userDTO.getCorpNo() == null) {
			return null;
		}
		return userDTO.getCorpNo();
	}
	
	//기업회원은 초기에 이름이 없음 
	public String getName() {
		if(userDTO.getName() == null) {
			return userDTO.getEmail();
		}
		return userDTO.getName();
	}
	
	//프로필 사진 가져오기 
	public String getProfileImage() {
		if(userDTO.getProfileImage() == null) {
			return null;
		}
		return userDTO.getProfileImage();
	}
	
	//Role 객체말고 String가져오기
	public String getRole() {
		if(userDTO.getRole() == null) {
			return null;
		}
		return userDTO.getRole();
	}
	
	//계정 활동제한 여부 가져오기
	public Integer getActiveStatus() {
		if(userDTO.getActiveStatus() == null) {
			return null;
		}
		return userDTO.getActiveStatus();
	}
	
	//이정도만 가져올까?
	
	

	

	
	
}
