package com.icecream.common.model.pojo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class GoodStore {
    private Integer id;

    private Integer goodNum;

    private Integer warnNum;

    private Integer goodId;

    private String specId;

    private Integer ctime;

    private Integer mtime;

}