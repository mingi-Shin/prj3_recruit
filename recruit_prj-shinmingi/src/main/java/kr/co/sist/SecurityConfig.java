package kr.co.sist;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.sist.admin.login.AdminDetailsServiceImpl;
import kr.co.sist.jwt.AdminCustomLoginFailureHandler;
import kr.co.sist.jwt.AdminCustomLoginSuccessHandler;
import kr.co.sist.jwt.CustomLoginFailureHandler;
import kr.co.sist.jwt.CustomLoginSuccessHandler;
import kr.co.sist.jwt.NewLoginFilter;
import kr.co.sist.jwt.JWTFIlter;
import kr.co.sist.jwt.JWTUtil;
import kr.co.sist.login.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JWTUtil jwtUtil;
  private final UserDetailsServiceImpl userDetailsServiceImpl;
  private final AdminDetailsServiceImpl adminDetailsServiceImpl;
  private final AccessDeniedHandler accessDeniedHandler;
  private final AdminCustomLoginFailureHandler adminCustomLoginFailureHandler;
  private final AdminCustomLoginSuccessHandler adminCustomLoginSuccessHandler;
    
  public SecurityConfig(JWTUtil jwtUtil, 
		UserDetailsServiceImpl userDetailsServiceImpl, 
		AdminDetailsServiceImpl adminDetailsServiceImpl, 
		AccessDeniedHandler accessDeniedHandler,
		AdminCustomLoginFailureHandler adminCustomLoginFailureHandler, 
		AdminCustomLoginSuccessHandler adminCustomLoginSuccessHandler) 
  {
  	this.jwtUtil = jwtUtil;
		this.userDetailsServiceImpl = userDetailsServiceImpl;
		this.adminDetailsServiceImpl = adminDetailsServiceImpl;
		this.accessDeniedHandler = accessDeniedHandler;
		this.adminCustomLoginFailureHandler = adminCustomLoginFailureHandler;
		this.adminCustomLoginSuccessHandler = adminCustomLoginSuccessHandler;
	}
    
	/**
	 * 사용자용 AuthenticationManager - JWT 로그인용
	 * @Primary 어노테이션으로 기본 AuthenticationManager로 지정
	 */
	@Bean(name = "userAuthenticationManager")
	@Primary
	public AuthenticationManager userAuthenticationManager(BCryptPasswordEncoder passwordEncoder) {
	    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	    provider.setUserDetailsService(userDetailsServiceImpl);
	    provider.setPasswordEncoder(passwordEncoder);
	    return new ProviderManager(provider);
	}
	
	/**
	 * 관리자용 AuthenticationManager - 폼 로그인용
	 */
	@Bean(name = "adminAuthenticationManager")
	public AuthenticationManager adminAuthenticationManager(BCryptPasswordEncoder passwordEncoder) {
	    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	    provider.setUserDetailsService(adminDetailsServiceImpl);
	    provider.setPasswordEncoder(passwordEncoder);
	    return new ProviderManager(provider);
	}

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }
  /**
   * 관리자 보안 설정 - 폼 로그인 방식
   */
  @Bean
  @Order(1)
  public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
      http.securityMatcher("/admin/**")
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/admin/admin_login", "/admin/css/**", "/admin/js/**").permitAll()
              .requestMatchers(HttpMethod.GET, "/admin/**").hasAnyRole("사원", "대리", "과장", "팀장", "SUPER")
              .requestMatchers(HttpMethod.POST, "/admin/**").hasAnyRole("대리", "과장", "팀장", "SUPER")
              .requestMatchers(HttpMethod.PUT, "/admin/**").hasAnyRole("사원", "대리", "과장", "팀장", "SUPER")
              .requestMatchers(HttpMethod.DELETE, "/admin/**").hasAnyRole("팀장", "SUPER")
              .anyRequest().authenticated()
          )
          .csrf(csrf -> csrf.disable())
          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
          .formLogin(form -> form
              .loginPage("/admin/admin_login")
              .loginProcessingUrl("/admin/login_process")
              .usernameParameter("admin_email")
              .passwordParameter("admin_password")
              .failureHandler(adminCustomLoginFailureHandler)
              .successHandler(adminCustomLoginSuccessHandler)
              .permitAll()
          )
          .logout(logout -> logout
              .logoutUrl("/admin/logout")
              .logoutSuccessUrl("/admin/admin_login")
              .invalidateHttpSession(true)
              .deleteCookies("JSESSIONID")
          );
      
      return http.build();
  }
  
  /**
   * 사용자 보안 설정 - JWT REST API 방식
   */
  @Bean
  @Order(2)
  public SecurityFilterChain userFilterChain(HttpSecurity http, @Qualifier("userAuthenticationManager") AuthenticationManager userAuthManager) throws Exception {
      http.securityMatcher("/**")
	      .authorizeHttpRequests(auth -> auth
	          // 인증 없이 접근 허용 - 정적 리소스
	          .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()
	          .requestMatchers("*.css", "*.js", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.svg", "*.ico").permitAll()
	          // 인증 없이 접근 허용 - API 및 페이지
	          .requestMatchers("/api/auth/**", "/login", "/register", "/corp/main", "/").permitAll()
	          // USER 권한 필요
	          .requestMatchers("/user/resume/**", "/user/mypage", "/apply").hasRole("USER")
	          // CORP 권한 필요
	          .requestMatchers("/corp/applicant", "/corp/jobPostingForm", 
	                         "/corp/myJobPostingListPage", "/corp/talentPool/**", 
	                         "/corp/image/**", "/corp/info/**").hasRole("CORP")
	          .anyRequest().permitAll()
	      )
        .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler))
        .csrf(csrf -> csrf.disable())
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable())
        .logout(logout -> logout.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationManager(userAuthManager) // 명시적으로 사용자용 AuthenticationManager 지정
        // JWT 필터 추가
        .addFilterBefore(new JWTFIlter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
        .addFilterAt(new NewLoginFilter(userAuthManager, jwtUtil), 
                    UsernamePasswordAuthenticationFilter.class);

      return http.build();
  }
    
}