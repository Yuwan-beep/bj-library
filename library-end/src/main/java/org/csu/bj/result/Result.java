package org.csu.bj.result;

public class Result {
    //响应码
    private int code;
    private Object data;

    public Result(int code){
        this.code=code;
    }
    public Result(int code,Object data){
        this.code=code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
