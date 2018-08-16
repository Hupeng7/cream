package com.icecream.common.model.pojo;


import lombok.Data;

@Data
public class GoodsSpec {

    private String id;

    private String goodsSn;

    private String spec;

    private String specOpt;

    private String specPic;

    private Integer price;

    private Integer stock;

    private Integer ctime;

    private Integer mtime;

    private Short isInuse;

}