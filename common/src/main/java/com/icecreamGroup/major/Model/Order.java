package com.icecreamGroup.major.Model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {

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
    private Integer cdKey;

    /**
     * 兑换码sn
     */
    private Integer cdSn;

    /**
     * 创建时间
     */
    private Integer ctime;

    public Integer getMtime() {
        return mtime;
    }

    public void setMtime(Integer mtime) {
        this.mtime = mtime;
    }

    public Integer getCtime() {
        return ctime;
    }

    public void setCtime(Integer ctime) {
        this.ctime = ctime;
    }

    /**
     * 修改时间
     */
    private Integer mtime;

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getCreater() {
        return creater;
    }

    public void setCreater(Integer creater) {
        this.creater = creater;
    }

    public String getParentOrderNo() {
        return parentOrderNo;
    }

    public void setParentOrderNo(String parentOrderNo) {
        this.parentOrderNo = parentOrderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getIsDigital() {
        return isDigital;
    }

    public void setIsDigital(Integer isDigital) {
        this.isDigital = isDigital;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public Integer getGoodsCatCount() {
        return goodsCatCount;
    }

    public void setGoodsCatCount(Integer goodsCatCount) {
        this.goodsCatCount = goodsCatCount;
    }

    public Integer getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Integer goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsSmallUrl() {
        return goodsSmallUrl;
    }

    public void setGoodsSmallUrl(String goodsSmallUrl) {
        this.goodsSmallUrl = goodsSmallUrl;
    }

    public String getGoodsVideoUrl() {
        return goodsVideoUrl;
    }

    public void setGoodsVideoUrl(String goodsVideoUrl) {
        this.goodsVideoUrl = goodsVideoUrl;
    }

    public String getGoodsPageUrl() {
        return goodsPageUrl;
    }

    public void setGoodsPageUrl(String goodsPageUrl) {
        this.goodsPageUrl = goodsPageUrl;
    }

    public String getGoodsIco() {
        return goodsIco;
    }

    public void setGoodsIco(String goodsIco) {
        this.goodsIco = goodsIco;
    }

    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsInstructions() {
        return goodsInstructions;
    }

    public void setGoodsInstructions(String goodsInstructions) {
        this.goodsInstructions = goodsInstructions;
    }

    public Integer getOnactivitiesTime() {
        return onactivitiesTime;
    }

    public void setOnactivitiesTime(Integer onactivitiesTime) {
        this.onactivitiesTime = onactivitiesTime;
    }

    public Integer getOffactivitiesTime() {
        return offactivitiesTime;
    }

    public void setOffactivitiesTime(Integer offactivitiesTime) {
        this.offactivitiesTime = offactivitiesTime;
    }

    public String getActivitiesAddress() {
        return activitiesAddress;
    }

    public void setActivitiesAddress(String activitiesAddress) {
        this.activitiesAddress = activitiesAddress;
    }

    public Integer getIsShip() {
        return isShip;
    }

    public void setIsShip(Integer isShip) {
        this.isShip = isShip;
    }

    public Integer getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Integer goodsCount) {
        this.goodsCount = goodsCount;
    }

    public Integer getRefundableCount() {
        return refundableCount;
    }

    public void setRefundableCount(Integer refundableCount) {
        this.refundableCount = refundableCount;
    }

    public BigDecimal getExpPrice() {
        return expPrice;
    }

    public void setExpPrice(BigDecimal expPrice) {
        this.expPrice = expPrice;
    }

    public Integer getShipTime() {
        return shipTime;
    }

    public void setShipTime(Integer shipTime) {
        this.shipTime = shipTime;
    }

    public Integer getShipCompletedTime() {
        return shipCompletedTime;
    }

    public void setShipCompletedTime(Integer shipCompletedTime) {
        this.shipCompletedTime = shipCompletedTime;
    }

    public String getShipBestTime() {
        return shipBestTime;
    }

    public void setShipBestTime(String shipBestTime) {
        this.shipBestTime = shipBestTime;
    }

    public Integer getIsReceipt() {
        return isReceipt;
    }

    public void setIsReceipt(Integer isReceipt) {
        this.isReceipt = isReceipt;
    }

    public Integer getConfirmReceiptTime() {
        return confirmReceiptTime;
    }

    public void setConfirmReceiptTime(Integer confirmReceiptTime) {
        this.confirmReceiptTime = confirmReceiptTime;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getIsPay() {
        return isPay;
    }

    public void setIsPay(Integer isPay) {
        this.isPay = isPay;
    }

    public BigDecimal getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }

    public Integer getPayTime() {
        return payTime;
    }

    public void setPayTime(Integer payTime) {
        this.payTime = payTime;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getChangePrice() {
        return changePrice;
    }

    public void setChangePrice(BigDecimal changePrice) {
        this.changePrice = changePrice;
    }

    public Integer getChangeType() {
        return changeType;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    public Integer getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Integer changeTime) {
        this.changeTime = changeTime;
    }

    public BigDecimal getInsurePrice() {
        return insurePrice;
    }

    public void setInsurePrice(BigDecimal insurePrice) {
        this.insurePrice = insurePrice;
    }

    public BigDecimal getPackFee() {
        return packFee;
    }

    public void setPackFee(BigDecimal packFee) {
        this.packFee = packFee;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getAddressid() {
        return addressid;
    }

    public void setAddressid(Integer addressid) {
        this.addressid = addressid;
    }

    public String getAddressee() {
        return addressee;
    }

    public void setAddressee(String addressee) {
        this.addressee = addressee;
    }

    public String getExpressCompany() {
        return expressCompany;
    }

    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public String getLogistics() {
        return logistics;
    }

    public void setLogistics(String logistics) {
        this.logistics = logistics;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getItucode() {
        return itucode;
    }

    public void setItucode(String itucode) {
        this.itucode = itucode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInvType() {
        return invType;
    }

    public void setInvType(String invType) {
        this.invType = invType;
    }

    public String getInvPayee() {
        return invPayee;
    }

    public void setInvPayee(String invPayee) {
        this.invPayee = invPayee;
    }

    public String getInvContent() {
        return invContent;
    }

    public void setInvContent(String invContent) {
        this.invContent = invContent;
    }

    public BigDecimal getInvTaxPrice() {
        return invTaxPrice;
    }

    public void setInvTaxPrice(BigDecimal invTaxPrice) {
        this.invTaxPrice = invTaxPrice;
    }

    public String getShipNumber() {
        return shipNumber;
    }

    public void setShipNumber(String shipNumber) {
        this.shipNumber = shipNumber;
    }

    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getDiscountOtherPrice() {
        return discountOtherPrice;
    }

    public void setDiscountOtherPrice(BigDecimal discountOtherPrice) {
        this.discountOtherPrice = discountOtherPrice;
    }

    public String getUserNote() {
        return userNote;
    }

    public void setUserNote(String userNote) {
        this.userNote = userNote;
    }

    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUserBehavior() {
        return userBehavior;
    }

    public void setUserBehavior(String userBehavior) {
        this.userBehavior = userBehavior;
    }

    public Integer getManufactureTime() {
        return manufactureTime;
    }

    public void setManufactureTime(Integer manufactureTime) {
        this.manufactureTime = manufactureTime;
    }

    public Integer getIsSynced() {
        return isSynced;
    }

    public void setIsSynced(Integer isSynced) {
        this.isSynced = isSynced;
    }

    public Integer getSyncedTime() {
        return syncedTime;
    }

    public void setSyncedTime(Integer syncedTime) {
        this.syncedTime = syncedTime;
    }

    public BigDecimal getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(BigDecimal promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public BigDecimal getGainsharingPrice() {
        return gainsharingPrice;
    }

    public void setGainsharingPrice(BigDecimal gainsharingPrice) {
        this.gainsharingPrice = gainsharingPrice;
    }

    public Integer getIsMass() {
        return isMass;
    }

    public void setIsMass(Integer isMass) {
        this.isMass = isMass;
    }

    public Integer getCdType() {
        return cdType;
    }

    public void setCdType(Integer cdType) {
        this.cdType = cdType;
    }

    public Integer getCdKey() {
        return cdKey;
    }

    public void setCdKey(Integer cdKey) {
        this.cdKey = cdKey;
    }

    public Integer getCdSn() {
        return cdSn;
    }

    public void setCdSn(Integer cdSn) {
        this.cdSn = cdSn;
    }
}
