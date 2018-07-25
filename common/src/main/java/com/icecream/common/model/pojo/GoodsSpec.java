package com.icecream.common.model.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoodsSpec {
    private String id;

    private String goodId;

    private String spec;

    private Integer price;

    private Integer stock;

    private Integer isInuse;

    private LocalDateTime createTime;

    private String specOpt;

    private LocalDateTime updateTime;


}