package cn.enilu.flash.web.exception;

/**
 * 异常编码
 * @author  enilu(eniluzt@qq.com)
 */
public class ErrorCode {
	/**
	 * 系统内部错误
	 */
	public static final ErrorCode INTERNAL_ERROR = new ErrorCode(-1, "系统内部错误");
	/**
	 * 数据校验错误
	 */
	public static final ErrorCode VALIDATION_ERROR = new ErrorCode(-2, "数据校验错误");
	/**
	 * 业务异常
	 */
	public static final ErrorCode BUSINESS_ERROR = new ErrorCode(-3, "业务异常");

	public final int code;
	public final String message;

	/**
	 *
	 * @param code 异常编码
	 * @param message 异常说明
	 */
	public ErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
