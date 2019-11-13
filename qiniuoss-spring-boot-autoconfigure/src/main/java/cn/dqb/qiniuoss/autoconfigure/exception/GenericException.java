package cn.dqb.qiniuoss.autoconfigure.exception;

import java.io.Serializable;
import lombok.Data;

@Data
public class GenericException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = 1L;
	String errorCode;
	String errorMessage;
	Object data;

	public GenericException() {
	}

	public GenericException(String message) {
		super(message);
	}

	public GenericException(Exception oriEx) {
		super(oriEx);
	}

	public GenericException(Exception oriEx, String message) {
		super(message, oriEx);
	}

	public GenericException(Throwable oriEx) {
		super(oriEx);
	}

	public GenericException(String message, Exception oriEx) {
		super(message, oriEx);
	}

	public GenericException(String message, Throwable oriEx) {
		super(message, oriEx);
	}

	@Override
	public String toString() {
		return "GenericException{" + "errorCode='" + errorCode + '\'' + ", errorMessage='" + errorMessage + '\'' + '}';
	}
}