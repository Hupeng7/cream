package com.icecream.common.model.requstbody;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品库存模型
 */
@Data
public class GoodsStoreModel implements Serializable {

    //商品库存
    private Integer store;

    //商品唯一编号
    private String goodsSn;

    //多规格or单规格商品的价格
    private BigDecimal finalPrice;

    //商品总限购
    private Integer limit;

    //规格描述
    private String spec;

}
