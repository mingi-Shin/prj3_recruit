package kr.co.sist;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import kr.co.sist.admin.login.AdminDetailsServiceImpl;
import kr.co.sist.jwt.AdminCustomLoginFailureHandler;
import kr.co.sist.jwt.AdminCustomLoginSuccessHandler;
import kr.co.sist.jwt.CustomLoginFailureHandler;
import kr.co.sist.jwt.CustomLoginSuccessHandler;
import kr.co.sist.jwt.CustomUsernamePasswordAuthenticationFilter;
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
    private final AuthenticationConfiguration authenticationConfiguration;
    public SecurityConfig(JWTUtil jwtUtil, UserDetailsServiceImpl userDetailsServiceImpl, AdminDetailsServiceImpl adminDetailsServiceImpl, AccessDeniedHandler accessDeniedHandler
    		,AdminCustomLoginFailureHandler adminCustomLoginFailureHandler, AdminCustomLoginSuccessHandler adminCustomLoginSuccessHandler, AuthenticationConfiguration authenticationConfiguration) {
        this.jwtUtil = jwtUtil;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.adminDetailsServiceImpl = adminDetailsServiceImpl;
        this.accessDeniedHandler = accessDeniedHandler;
        this.adminCustomLoginFailureHandler = adminCustomLoginFailureHandler;
        this.adminCustomLoginSuccessHandler = adminCustomLoginSuccessHandler;
        this.authenticationConfiguration = authenticationConfiguration;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // 🔥 문제의 AuthenticationManager Bean 삭제 또는 수정
    // 각 필터체인에서 개별적으로 userDetailsService를 설정하도록 변경

    
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
            .userDetailsService(adminDetailsServiceImpl)  // 🔥 직접 설정
            .formLogin(auth -> auth
                .loginPage("/admin/admin_login")
                .loginProcessingUrl("/admin/login_process")
                .usernameParameter("admin_email")
                .passwordParameter("admin_password")
                .failureHandler(adminCustomLoginFailureHandler)
                .successHandler(adminCustomLoginSuccessHandler)
                .permitAll()
            )
            .logout(auth -> auth
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/admin_login")
                .invalidateHttpSession(true)
            );
        
        return http.build();
    }
    



    @Bean
    @Order(2)
 // SecurityFilterChain 설정 - 사용자 요청에 따른 접근 제어 및 인증/인가 처리
    public SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/**")
            .authorizeHttpRequests(auth -> auth
                // 인증 없이 접근 허용하는 경로
                .requestMatchers("/login", "/api/auth/login", "/api/auth/register", "/register", "/images/**", "/reissue", "/corp/main", "/reissue").permitAll()
                //정적자료 제어 (static 아래) 혹은 anyRequest()로 퉁치거나 
                //.requestMatchers("/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.jpeg", "/**/*.gif", "/**/*.svg", "/**/*.png", "/**/*.ttf", "/**/*.svg").permitAll()
                // USER 권한 필요 경로
                .requestMatchers("/user/resume/**", "/user/mypage", "/apply").hasRole("USER")
                // CORP 권한 필요 경로
                .requestMatchers("/corp/applicant", "/corp/jobPostingForm", "/corp/myJobPostingListPage", "/corp/talentPool/**", "/corp/image/**", "/corp/info/**").hasRole("CORP")
                .anyRequest().permitAll() // 그 외는 모두 허용
            )
            // 권한 부족 시 커스텀 핸들러 작동
            .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler))
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (RESTful API에서는 일반적으로 비활성)
            .userDetailsService(userDetailsServiceImpl) // 사용자 인증 서비스 직접 등록
            /**
            기본 formLogin과 logout은 폐기 
            .formLogin(auth -> auth
                .loginPage("/login")
                .loginProcessingUrl("/loginProcess")
                .usernameParameter("email")
                .passwordParameter("password")
                .failureHandler(new CustomLoginFailureHandler())
                .successHandler(new CustomLoginSuccessHandler(jwtUtil))
                //.defaultSuccessUrl("/main")  // 로그인 성공 시 이동, successHandler를 쓰면 이 옵션은 무시됨
                .permitAll()
            )
            .logout(auth -> auth
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "access", "refresh", "Authorization")
            )
            */
            // formLogin, logout 비활성화 (커스텀 필터로 대체)
            .formLogin(form -> form.disable())
            .logout(form -> form.disable())
            // httpBasic 비활성화 (왜? -> 
            .httpBasic(basic -> basic.disable())
            // 세션 관리 정책을 STATELESS로 설정 (JWT 사용) (왜? -> 
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 커스텀 로그인 필터 등록 (UsernamePasswordAuthenticationFilter 위치에, At)
            .addFilterAt(customUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            // JWT 검증 필터 등록 (로그인 필터 이후)
            .addFilterAfter(new JWTFIlter(jwtUtil), CustomUsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    /**
     * 	커스텀 로그인 필터를 빈 등록 
     */
    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter() throws Exception {
    	
    	// JWTUtil만 생성자로 주입
      CustomUsernamePasswordAuthenticationFilter filter = new CustomUsernamePasswordAuthenticationFilter(jwtUtil);
      
      // AuthenticationManager는 setter로 주입 (필수!)
      // 이게 무슨 일을 하지?
      filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
      
      // 로그인 URL 설정 (선택사항, 기본값: /api/auth/login)
      // filter.setFilterProcessesUrl("/api/auth/login");
    	
    	return filter;
    }
}