package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Table(name = "order_info")
public class Order {

    /**
     * 全局id
     */
    private Long id;

    /**
     * sid 1郑爽 2杨幂
     */
    private Integer sid;

    /**
     * 用户ID
     */
    private Integer uid;

    /**
     * 默认-1 系统  创建者id
     */
    private Integer creater;

    /**
     *父订单 退款的时候填写
     */
    private String parentOrderNo;

    /**
     * 18位订单号 2017
     */
    private String orderNo;

    /**
     * 是否虚拟类商品，0，否；1，是
     */
    private Integer isDigital;

    /**
     * 订单类型 1 平台交易 2 担保交易
     */
    private Integer orderType;

    /**
     * 报表类型 1 充值账单 2 商城账单
     * 3 赠送积分 4 消耗积分
     */
    private Integer reportType;

    /**
     * 商品种类数量
     */
    private Integer goodsCatCount;

    /**
     * 1 普通商品 2 活动商品
     */
    private Integer goodsType;

    /**
     * 商品Id
     */
    private String goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品展示小图片Url
     */
    private String goodsSmallUrl;

    /**
     * 商品视频展示Url
     */
    private String goodsVideoUrl;

    /**
     * 商品展示url
     */
    private String goodsPageUrl;

    /**
     * 商品Ico
     */
    private String goodsIco;

    /**
     * 商品价格
     */
    private BigDecimal goodsPrice;

    /**
     * 商品使用说明
     */
    private String goodsInstructions;

    /**
     * 活动开始时间
     */
    private Integer onactivitiesTime;

    /**
     * 活动结束时间
     */
    private Integer offactivitiesTime;

    /**
     * 活动地点
     */
    private String activitiesAddress;

    /**
     * 是否已经收货 0 未收货 1 已收货
     */
    private Integer isShip;

    /**
     * 商品总数
     */
    private Integer goodsCount;

    /**
     * 可退款总数
     */
    private Integer refundableCount;

    /**
     * 经验值
     */
    private BigDecimal expPrice;

    /**
     * 发货时间
     */
    private Integer shipTime;

    /**
     * 发货完成时间
     */
    private Integer shipCompletedTime;

    /**
     * 最佳完成时间
     */
    private String shipBestTime;

    /**
     * 是否已经发货 0未发货 1已发货
     */
    private Integer isReceipt;

    /**
     * 确认收货时间
     */
    private Integer confirmReceiptTime;

    /**
     * 订单的状态;-1 无效订单,0 待付款 ,1未处理,
     * 2 已受理 3 已发货,4 已完成 ,5 已关闭,6 已取消,7退货，
     */
    private Integer orderStatus;

    /**
     * 订单的状态;-1 无效退款,0 待退款 ,1退款中,2 退款成功 3 退款失败
     */
    private Integer refundStatus;

    /**
     * 状态  1正常  0禁用  -1 默认
     */
    private Integer status;

    /**
     * 用户提现账户
     */
    private String  account;

    /**
     * 是否已经支付 0 未支付 1 完成支付
     */
    private Integer isPay;

    /**
     * 实际支付价格
     */
    private BigDecimal payPrice;

    /**
     * 支付时间
     */
    private Integer payTime;

    /**
     * 1 支付宝 2 微信 3 信用卡 4 星星 5 月亮
     */
    private Integer paymentType;

    /**
     * 变动价格
     */
    private BigDecimal changePrice;

    /**
     * 1 人民币 2 美金 3 欧元 4 星星 5 月亮
     */
    private Integer changeType;

    /**
     * 变动时间
     */
    private Integer changeTime;

    /**
     * 保价费用
     */
    private BigDecimal insurePrice;

    /**
     * 包装费用
     */
    private BigDecimal packFee;

    /**
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 收货地址id
     */
    private Integer addressid;

    /**
     * 收件人
     */
    private String addressee;

    /**
     * 快递公司
     */
    private String expressCompany;

    /**
     * 快递单号
     */
    private String expressNo;

    /**
     * 物流信息
     */
    private String logistics;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 地区
     */
    private String district;

    /**
     * 地区
     */
    private String zipcode;

    /**
     * 电信码
     */
    private String itucode;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 保价费用
     */
    private String email;

    /**
     * 发票类型
     */
    private String invType;

    /**
     * 发票抬头
     */
    private String invPayee;

    /**
     * 发票内容
     */
    private String invContent;

    /**
     * 发票税额
     */
    private BigDecimal invTaxPrice;

    /**
     * 快递单号
     */
    private String shipNumber;

    /**
     * 优惠券号码
     */
    private String couponNo;

    /**
     * 使用优惠券优惠的金额
     */
    private BigDecimal discountPrice;

    /**
     * 其他优惠金额
     */
    private BigDecimal discountOtherPrice;

    /**
     * 客户备注
     */
    private String userNote;

    /**
     * 订单备注
     */
    private String orderNote;

    /**
     * 备注
     */
    private String note;

    /**
     * 用户行为
     */
    private String userBehavior;

    /**
     * 工厂制作时间
     */
    private Integer manufactureTime;

    /**
     * 是否已经同步，0，否；1，是
     */
    private Integer isSynced;

    /**
     * 同步完成时间
     */
    private Integer syncedTime;

    /**
     * 推广费用
     */
    private BigDecimal promotionPrice;

    /**
     * 分成金额
     */
    private BigDecimal gainsharingPrice;

    /**
     * 是否已成团
     */
    private Integer isMass;

    /**
     * 1 纯文字  2 二维码  3 条形码
     */
    private Integer cdType;

    /**
     * 兑换码
     */
    private String  cdKey;

    /**
     * 兑换码sn
     */
    private String cdSn;

    /**
     * 创建时间
     */
    private Integer ctime;


    /**
     * 修改时间
     */
    private Integer mtime;


}
