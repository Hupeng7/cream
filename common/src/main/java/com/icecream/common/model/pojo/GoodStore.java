package com.icecream.common.model.pojo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class GoodStore {
    private String id;

    private Integer goodNum;

    private Integer warnNum;

    private String goodId;

    private String specId;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;

}