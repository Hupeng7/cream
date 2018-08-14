package com.icecream.common.model.pojo;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
public class WechatpayNotifyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "appid不能为空")
    private String appid;

    private Integer uid;

    private String attach;

    private Integer ctime;

    private Integer mtime;

    @NotBlank(message = "付款银行不能为空")
    private String bank_type;

    private String fee_type;

    private String is_subscribe;

    @NotBlank(message = "商户号不能为空")
    private String mch_id;

    @NotBlank(message = "随机字符串不能为空")
    private String nonce_str;

    @NotBlank(message = "用户在商户appid下的唯一标识不能为空")
    private String openid;

    @NotBlank(message = "商户自生成订单号不能为空")
    private String out_trade_no;

    @NotBlank(message = "返回状态码不能为空")
    private String result_code;

    //默认与result_code一致
    private String return_code;

    @NotBlank(message = "签名不能为空")
    private String sign;

    @NotBlank(message = "支付完成时间不能为空")
    private String time_end;

    @NotNull
    @Range(min = 500, max = 50000, message = "total_fee最低500,最高50000")
    private Integer total_fee;

    @NotBlank(message = "交易类型不能为空")
    private String trade_type;

    @NotBlank(message = "微信支付订单号不能为空")
    private String transaction_id;

    private String return_msg;

    //微信支付分配的终端设备号
    private String device_info;

    //微信支付返回的支付错误代码编号(SYSTEMERROR)
    private String errCode;

    //微信支付返回的支付错误解释说明(系统说明)
    private String errCodeDes;

    @NotNull
    @Range(min = 500, max = 50000, message = "total_fee最低500,最高50000")
    private Integer cash_fee;

    //后面的属性基本用不到,默认值即可----->
    private String cash_fee_type;

    private Integer coupon_fee;

    private Integer coupon_count;

    private String coupon_id_$n;

    private Integer coupon_fee_$n;
    //--------------------------------->
}