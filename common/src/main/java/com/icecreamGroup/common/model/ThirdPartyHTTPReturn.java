package com.icecreamGroup.common.model;

import lombok.Data;

/**
 * @author Mr_h
 * @version 1.0
 * description: 第三方平台返回参数（qq）
 * create by Mr_h on 2018/6/14 0014
 */
@Data
public class ThirdPartyHTTPReturn {

    private Integer ret;//返回码
    private String msg; //返回信息
    private String nickName; //用户在qq空间的昵称
    private String figureUrl; //大小为30x30像素的QQ空间头像Url
    private String figureUrl_1; //大小为50x50像素的QQ空间头像Url
    private String figureUrl_2; //大小为100x100像素的QQ空间头像Url
    private String figureUrl_qq_1;//大小为30x30像素的QQ头像Url
    private String figureUrl_qq_2;//大小为50x50像素的QQ头像Url
    private String gender; //性别。如果获取不到默认为'男'
    private Integer isYellowVip; //是否是黄钻
    private Integer vip; //是否是vip
    private Integer yellowVipLevel;//黄钻等级
    private Integer level; //vip等级
    private Integer isYellowYearVip;//是否是黄钻年费会员
}
