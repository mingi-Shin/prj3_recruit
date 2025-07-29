package kr.co.sist.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CipherUtil {
	//	properteis에 
	// 	prj.key=sist0123456789
	//	prj.salt=a1b2c3d4a1b2c3d4
	// 	입력!
	@Value("${prj.key}")
	private String key;
	@Value("${prj.salt}")
	private String salt; //16진수로 변환가능한 8byte문자 대입 
	
	public String hashText(String plainText) {
		String hashedValue = "";
		
		//1.단방향 해시 객체 생성
		PasswordEncoder pe = new BCryptPasswordEncoder();
		hashedValue = pe.encode(plainText);
		
		return hashedValue;
	}
	
	public boolean hashMatches(String plainText, String bcryptText) {
		boolean flag = false;
		
		PasswordEncoder pe = new BCryptPasswordEncoder();
		flag =pe.matches(plainText, bcryptText);
		
		return flag;
	}
	
	//*cipher는 원칙적으로 양방향 암호화(복호화 가능한 암호화)**를 의미 (이메일, 이름, 휴대폰, 도로명주소, 상세주소)
	public String encryptText(String plainText) {
		String cipherText = "";
		System.out.println("디버깅 cipherText() 키와 솔트 = " + key + " / " + salt);
		TextEncryptor te = Encryptors.text(key, salt);
		cipherText = te.encrypt(plainText);

		return cipherText; //암호화 된 텍스트 return
	}
	
	
	public String decryptText(String plainText) {
		String cipherText = "";
		TextEncryptor te = Encryptors.text(key, salt);
		cipherText = te.decrypt(plainText);
		
		return cipherText;
	}
	

}
