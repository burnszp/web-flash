package cn.enilu.flash.web.exception;

/**
 * 业务类型的异常封装
 *
 * @author enilu(enilutzt@qq.com)
 */
public class BusinessException extends ApplicationException {

    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
}
