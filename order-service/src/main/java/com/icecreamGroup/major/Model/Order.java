package com.icecreamGroup.major.Model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {
    private Integer sid;

    private Integer uid;

    private Integer creater;

    private String parentOrderNo;

    private String orderNo;

    private Integer isDigital;

    private Integer orderType;

    private Integer reportType;

    private Integer goodsCatCount;

    private Integer goodsType;

    private String goodsId;

    private String goodsName;

    private String goodsSmallUrl;

    private String goodsVideoUrl;

    private String goodsPageUrl;

    private String goodsIco;

    private BigDecimal goodsPrice;

    private String goodsInstructions;

    private Integer onactivitiesTime;

    private Integer offactivitiesTime;

    private String activitiesAddress;

    private Integer isShip;

    private Integer goodsCount;

    private Integer refundableCount;

    private BigDecimal expPrice;

    private Integer shipTime;

    private Integer shipCompletedTime;

    private String shipBestTime;

    private Integer isReceipt;

    private Integer confirmReceiptTime;

    private Integer orderStatus;

    private Integer refundStatus;

    private Integer status;

    private String account;

    private Integer isPay;

    private BigDecimal payPrice;

    private Integer payTime;

    private Integer paymentType;

    private BigDecimal changePrice;

    private Integer changeType;

    private Integer changeTime;

    private BigDecimal insurePrice;

    private BigDecimal packFee;

    private BigDecimal amount;

    private String address;

    private Integer addressid;

    private String addressee;

    private String expressCompany;

    private String expressNo;

    private String logistics;

    private String country;

    private String province;

    private String city;

    private String district;

    private String zipcode;

    private String itucode;

    private String phone;

    private String email;

    private String invType;

    private String invPayee;

    private String invContent;

    private BigDecimal invTaxPrice;

    private String shipNumber;

    private String couponNo;

    private BigDecimal discountPrice;

    private BigDecimal discountOtherPrice;

    private String userNote;

    private String orderNote;

    private String note;

    private String userBehavior;

    private Integer manufactureTime;

    private Integer isSynced;

    private Integer syncedTime;

    private BigDecimal promotionPrice;

    private BigDecimal gainsharingPrice;

    private Integer isMass;

    private Integer cdType;

    private String cdKey;

    private String cdSn;

    private Integer ctime;

    private Integer mtime;


}