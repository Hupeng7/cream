package com.icecream.common.model.requstbody;

import com.icecream.common.model.pojo.Good;
import com.icecream.common.model.pojo.GoodsSpec;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/21 0021
 */
@Data
public class MitGoodsRedis implements Serializable {

    private Good good;

    private List<GoodsSpec> goodsSpec;
}
