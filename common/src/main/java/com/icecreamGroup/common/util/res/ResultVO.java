package com.icecreamGroup.common.util.res;

import lombok.Data;

@Data
public class ResultVO<T> {

    private Integer code;
    private String msg;
    private T result;

}
