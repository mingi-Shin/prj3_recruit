package kr.co.sist.globalController.Exceptions;

public class EmailSendException extends RuntimeException{

   public EmailSendException(String message) {
  	 super(message);
	}
}
