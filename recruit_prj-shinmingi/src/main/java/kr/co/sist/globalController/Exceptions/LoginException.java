package kr.co.sist.globalController.Exceptions;

import org.springframework.stereotype.Component;

public class LoginException extends RuntimeException {
	
	public LoginException(String message) {
		super(message);
	}

}
