package kr.co.sist;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import kr.co.sist.admin.login.AdminDetailsServiceImpl;
import kr.co.sist.jwt.AdminCustomLoginFailureHandler;
import kr.co.sist.jwt.AdminCustomLoginSuccessHandler;
import kr.co.sist.jwt.CustomLoginFailureHandler;
import kr.co.sist.jwt.CustomLoginSuccessHandler;
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
    public SecurityConfig(JWTUtil jwtUtil, UserDetailsServiceImpl userDetailsServiceImpl, AdminDetailsServiceImpl adminDetailsServiceImpl, AccessDeniedHandler accessDeniedHandler
    		,AdminCustomLoginFailureHandler adminCustomLoginFailureHandler, AdminCustomLoginSuccessHandler adminCustomLoginSuccessHandler) {
        this.jwtUtil = jwtUtil;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.adminDetailsServiceImpl = adminDetailsServiceImpl;
        this.accessDeniedHandler = accessDeniedHandler;
        this.adminCustomLoginFailureHandler = adminCustomLoginFailureHandler;
        this.adminCustomLoginSuccessHandler = adminCustomLoginSuccessHandler;
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
    public SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/**")
            .authorizeHttpRequests(auth -> auth
            		//맵핑 제어
                .requestMatchers("/login", "/register", "/images/**", "/reissue", "/corp/main").permitAll()
                //정적자료 제어 (static 아래) 혹은 anyRequest()로 퉁치거나 
                //.requestMatchers("/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.jpeg", "/**/*.gif", "/**/*.svg", "/**/*.png", "/**/*.ttf", "/**/*.svg").permitAll()
                .requestMatchers("/user/resume/**", "/user/mypage", "/apply").hasRole("USER")
                .requestMatchers("/corp/applicant", "/corp/jobPostingForm", "/corp/myJobPostingListPage", "/corp/talentPool/**", "/corp/image/**", "/corp/info/**").hasRole("CORP")
                .anyRequest().permitAll()
            )
            //로그인 Ok, but 권한이 없을 때 (403 Forbidden)
            .exceptionHandling(ex ->
            		ex.accessDeniedHandler(accessDeniedHandler) //AccessDeniedHandler는 인증은 되었지만 권한이 없는 경우에만 동작
            )
            .csrf(csrf -> csrf.disable())
            .userDetailsService(userDetailsServiceImpl)  // 🔥 직접 설정
            .formLogin(auth -> auth
                .loginPage("/login")
                .loginProcessingUrl("/loginProcess")
                .usernameParameter("email") //안하면 기본값 username
                .passwordParameter("password")
                .failureHandler(new CustomLoginFailureHandler())
                .successHandler(new CustomLoginSuccessHandler(jwtUtil))
                .permitAll()
            )
            .logout(auth -> auth
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "access", "refresh", "Authorization") 
            )
            .addFilterAfter(new JWTFIlter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}