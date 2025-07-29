package kr.co.sist.jwt;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import kr.co.sist.admin.AdminEntity;

public class CustomAdmin implements UserDetails {

    private final AdminEntity admin;

    public CustomAdmin(AdminEntity admin) {
        this.admin = admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.of(admin.getDeptRole(), admin.getJobRole())
                .filter(Objects::nonNull)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    @Override
    public String getUsername() {
        return admin.getAdminId();  // 이메일이 아니라 adminId가 아이디라면 이렇게
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 필요시 로직 추가 가능
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 필요시 로직 추가 가능
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 필요시 로직 추가 가능
    }

    @Override
    public boolean isEnabled() {
        return true; // 필요시 로직 추가 가능
    }
    
    // admin 객체에 접근하고 싶으면 getter 추가 가능
    public AdminEntity getAdmin() {
        return admin;
    }
}
