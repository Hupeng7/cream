package com.icecream.common.model.pojo;


import lombok.Data;

import javax.persistence.Id;

@Data
public class GoodsSpec {

    @Id
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