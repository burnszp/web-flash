package cn.enilu.flash.web.exception;

/**
 * 系统内部异常（无关业务）的异常封装
 *
 * @author enilu(eniluzt@qq.com)
 */
public class ApplicationException extends RuntimeException {
	private final ErrorCode errorCode;

	public ApplicationException(ErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public ApplicationException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ApplicationException(ErrorCode errorCode) {
		super(errorCode.message);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
