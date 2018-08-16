package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "goods_limit")
public class GoodsLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer uid;

    private Integer sid;

    private String goodsSn;

    private Integer goodsCount;

    private Boolean isSynced;

    private Integer syncedTime;

    private Integer ctime;

    private Integer mtime;

}