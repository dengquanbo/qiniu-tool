package cn.dqb.qiniuoss.autoconfigure.exception;

import lombok.Data;

/**
 * @date 2019/2/18 18:02
 */
@Data
public class QiniuOssException extends GenericException {

    public QiniuOssException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public QiniuOssException(Exception e, String errorCode, String errorMessage) {
        super(e, errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public QiniuOssException(Exception e, int errorCode, String errorMessage) {
        super(e, errorMessage);
        this.errorCode = String.valueOf(errorCode);
        this.errorMessage = errorMessage;
    }

    public QiniuOssException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public QiniuOssException(Exception oriEx) {
        super(oriEx);
    }

    public QiniuOssException(Throwable oriEx) {
        super(oriEx);
    }

    public QiniuOssException(String message, Exception oriEx) {
        super(message, oriEx);
        this.errorMessage = message;
    }

    public QiniuOssException(String message, Throwable oriEx) {
        super(message, oriEx);
        this.errorMessage = message;
    }

    public QiniuOssException(String message, int statusCode) {
        this.errorCode = String.valueOf(statusCode);
        this.errorMessage = message;
    }

    @Override
    public String toString() {
        return "QiniuException{" + "errorCode='" + errorCode + '\'' + ", errorMessage='" + errorMessage + '\'' + '}';
    }
}
