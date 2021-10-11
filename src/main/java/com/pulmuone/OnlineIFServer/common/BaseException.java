package com.pulmuone.OnlineIFServer.common;

public class BaseException extends RuntimeException {

	private static final long serialVersionUID = -4421260939522432513L;

	public BaseException() {
		super();
	}
	
	public BaseException(String message) {
        super(message);
    }
	
	public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public BaseException(Throwable cause) {
        super(cause);
    }
	
}
