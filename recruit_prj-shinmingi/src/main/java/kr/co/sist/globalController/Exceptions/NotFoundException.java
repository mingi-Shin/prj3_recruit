package kr.co.sist.globalController.Exceptions;

import org.springframework.stereotype.Component;

public class NotFoundException extends RuntimeException {
	
	public NotFoundException(String message) {
		super(message);
	}

}
