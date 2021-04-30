package com.pulmuone.OnlineIFServer.common;

public class IFException extends Exception {
	
	private static final long serialVersionUID = -6011397652445241930L;

	ResponseStatus status = null;
	
	public IFException(ResponseStatus status, String message, Object... clues) {
		super(message + createClues(clues));
		this.status = status;
	}
	
	public IFException(ResponseStatus status, String message, Exception e, Object... clues) {
		super(message + createClues(clues) + " | " + e.getMessage());
		this.status = status;
	}
	
	static String createClues(Object... clues) {
		StringBuffer buffer = new StringBuffer();
		 if(clues != null) {
			 for(int idx = 0, size = clues.length; idx < size; idx ++) {
				 if(idx == 0) 		buffer.append("[");
				 if(idx > 0) 		buffer.append(",");
				 buffer.append(clues[idx]);
				 if(idx == size-1) 	buffer.append("]");
			 }
		 }
		 return buffer.toString();
	}

	public String getErrorCode() {
		return this.status.value();
	}
	
	public String getErrorMessage() {
		return this.status.value() + " | " + this.status.phrase() + " | " + this.getMessage();
	}
	

}
