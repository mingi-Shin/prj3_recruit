package kr.co.sist.admin.login;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class CustomAdminLoginFilter extends UsernamePasswordAuthenticationFilter {

  public CustomAdminLoginFilter(AuthenticationManager authenticationManager) {
      super.setAuthenticationManager(authenticationManager);
      //setFilterProcessesUrl("/admin/login_process");
      setUsernameParameter("adminId");
      setPasswordParameter("admin_password");
  }
}
