package kr.co.sist.globalController.Exceptions;

public class TooManyRequestsException extends RuntimeException {
	public TooManyRequestsException(String message) {
    super(message);
	}
}
