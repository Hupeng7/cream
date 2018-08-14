package com.icecream.common.model.pojo;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import sun.security.util.ManifestEntryVerifier;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Table(name ="alipay_notify_record")
public class AlipayNotifyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "uid不能为空")
    private Integer uid;

    @NotNull(message = "notify_time不能为空")
    private Integer notify_time;

    @NotBlank(message = "notify_type不能为空")
    private String notify_type;

    @NotBlank(message ="notify_id不能为空")
    private String notify_id;

    @NotBlank(message = "app_ip不能为空")
    private String app_id;

    @NotBlank(message ="charset不能为空")
    private String charset;

    @NotBlank(message ="version不能为空")
    private String version;

    @NotBlank(message ="sign_type不能为空")
    private String sign_type;

    @NotBlank(message ="sign不能为空")
    private String sign;

    @NotBlank(message ="tradeNo不能为空")
    private String tradeNo;

    @NotBlank(message ="out_tradeNo不能为空")
    private String out_tradeNo;

    private String buyer_id;

    private String buyer_logon_id;

    @NotBlank(message ="seller_id不能为空")
    private String seller_id;

    @NotBlank(message ="seller_email不能为空")
    private String seller_email;

    @NotBlank(message ="trade_status不能为空")
    private String trade_status;

    @NotNull
    @Range(min = 5,max = 500,message ="不在充值区间内")
    private BigDecimal total_amount;

    @NotNull
    @Range(min = 5,max = 500,message ="不在充值区间内")
    private BigDecimal receipt_amount;

    @NotNull
    @Range(min = 5,max = 500,message ="不在充值区间内")
    private BigDecimal invoice_amount;

    @NotNull
    @Range(min = 5,max = 500,message ="不在充值区间内")
    private BigDecimal buyer_pay_amount;

    @NotNull
    @Range(min = 5,max = 500,message ="不在充值区间内")
    private BigDecimal point_amount;

    private BigDecimal refund_fee;

    private String subject;

    @NotBlank(message = "body不能为空")
    private String body;

    @NotNull(message = "gmt_create不能为空")
    private Integer gmt_create;

    @NotNull(message = "gmt_create不能为空")
    private Integer gmt_payment;

    private Integer gmt_refund;

    private Integer gmt_close;

    private String fund_bill_list;

    private String passback_params;

    private String voucher_detail_list;

    private Integer ctime;

    private Integer mtime;

    private String out_biz_no;
}