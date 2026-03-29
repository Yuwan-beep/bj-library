package org.csu.bj.api;

/**
 * @Description: Service 层异常
 * @author: gjt
 * @date: 2023/01/30
 */
public enum ServiceErrorCode implements IErrorCode {
    ;
    private long code;
    private String message;

    @Override
    public long getCode() {
        return 0;
    }

    @Override
    public String getMessage() {
        return null;
    }
}
