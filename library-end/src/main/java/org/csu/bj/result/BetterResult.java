package org.csu.bj.result;

import lombok.Data;

@Data
public class BetterResult<T> {
    private T data;
    private int state;

    public BetterResult(T _data, int _state){
        data = _data;
        state = _state;
    }
}
