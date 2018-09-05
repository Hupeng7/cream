package com.icecream.order.contants;

/**
 * @author Mr_h
 * @version 1.0
 * description: 订单常量池
 * create by Mr_h on 2018/7/2 0002
 */
public interface Contants {

    Integer DEFAULT_PAGE_CURRENT = 1;

    Integer DEFAULT_PAGE_SIZE = 15;


    /**
     * 业务流水常量
     */
    Integer TYPE_CHARGE = 1; //充值

    Integer TYPE_ORDER = 2; //订单

    Integer TYPE_VALUE_ADDED_SERVICE =3;//增值业务


    /**
     * 订单流水加减符号
     */
    String ADD = "+";
    String REDUCE = "-";


    /**
     * 用户行为加/减积分（星星）
     */
    Integer SIGN_IN = 1; //签到
    Integer CALL_COMMENT_TO_HEADLINE = 2; //留言上头条
    Integer CALL_COMMENT_TO_FRIST =3; //留言置顶
    Integer IMPROVING_PERSONAL_DATA = 4;//完善个人资料
    Integer POST_COMMENT =5 ;//发布留言
    Integer CHARGE = 6; //充值
    Integer THE_SECONDARY_REPLY = 7; //二级回复

}
