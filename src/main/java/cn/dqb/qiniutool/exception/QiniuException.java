package cn.dqb.qiniutool.exception;

import lombok.Data;

/**
 * @date 2019/2/18 18:02
 */
@Data
public class QiniuException extends GenericException {

    public QiniuException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public QiniuException(Exception e, String errorCode, String errorMessage) {
        super(e, errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public QiniuException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public QiniuException(Exception oriEx) {
        super(oriEx);
    }

    public QiniuException(Throwable oriEx) {
        super(oriEx);
    }

    public QiniuException(String message, Exception oriEx) {
        super(message, oriEx);
        this.errorMessage = message;
    }

    public QiniuException(String message, Throwable oriEx) {
        super(message, oriEx);
        this.errorMessage = message;
    }

    @Override
    public String toString() {
        return "QiniuException{" + "errorCode='" + errorCode + '\'' + ", errorMessage='" + errorMessage + '\'' + '}';
    }
}
