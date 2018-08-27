package com.icecream.common.model.requstbody;

import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/23 0023
 */
@Data
public class GoodsUpdateMessage {

    private String specId;

    private String goodsSn;

    private Integer goodsNum;

    private Integer uid;

    private Integer bought;

    private Integer sid;

    private Integer count;
}
