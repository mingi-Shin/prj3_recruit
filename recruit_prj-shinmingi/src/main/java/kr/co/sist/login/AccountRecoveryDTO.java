package kr.co.sist.login;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountRecoveryDTO {
  // 아이디 찾기용
  private String name;
  private String phone;

  // 비밀번호 찾기용
  private String email;
  private String newPassword;

  // getters, setters
}
