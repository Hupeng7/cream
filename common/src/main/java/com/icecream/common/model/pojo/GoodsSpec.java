package com.icecream.common.model.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoodsSpec {
    private String id;

    private String goodsSn;

    private String spec;

    private Integer price;

    private Integer stock;

    private Integer isInuse;

    private String specOpt;

    private Integer ctime;

    private Integer mtime;

}