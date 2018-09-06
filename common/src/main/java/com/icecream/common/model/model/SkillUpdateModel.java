package com.icecream.common.model.model;

import com.icecream.common.model.pojo.Order;
import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 秒杀模型
 * create by Mr_h on 2018/8/24 0024
 */
@Data
public class SkillUpdateModel {

    private GoodsUpdateMessage goodsUpdateMessage;

    private Order order;

}
