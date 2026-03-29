package org.csu.bj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code; // 状态码
    private String message; // 消息
    private T data; // 数据

    /**
     * 成功返回结果，默认状态码 200，消息为 "操作成功"
     * @param data 要返回的数据
     * @param <T> 数据的类型
     * @return 封装好的 Result 对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 失败返回结果，需要传入状态码和消息
     * @param code 状态码
     * @param message 消息
     * @param <T> 数据的类型
     * @return 封装好的 Result 对象
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
}
