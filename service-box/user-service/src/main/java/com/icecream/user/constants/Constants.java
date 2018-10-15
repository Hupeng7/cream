package com.icecream.user.constants;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/6 0006
 */
public class Constants {

    /**
     * 登陆方式
     */
    public static final int TYPE_SMS = 1;
    public static final int TYPE_ACCOUNT = 2;
    public static final int TYPE_AUTH_QQ = 3;
    public static final int TYPE_AUTH_WB = 4;
    public static final int TYPE_AUTH_WX = 5;

    /**
     * 支付方式
     */
    public static final int TYPE_ALI_PAY = 1;
    public static final int TYPE_WX_PAY = 2;

    /**
     * 工厂类型
     */
    public static final int LOGIN = 1;
    public static final int CHARGE = 2;

    /**
     * 头像框是否佩戴
     */
    public static final Integer WEAR = 1;
    public static final Integer NOTWEAR = 0;

    /**
     * 30天的秒数
     */
    public static final Integer MONTH_TO_SECOND = 2592000;

    /**
     * 用户个人频道展示排序
     */
    public static final String USER_CHANNELDISPLAY_SORT = "user_channeldisplay_sort";


}
