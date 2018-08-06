package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer sid;

    private Integer uid;

    private Integer type;

    private BigDecimal balance;

    private BigDecimal amount;

    private String note;

    private Integer bankcardId;

    private String sign;

    private String credential;

    private Integer status;

    private Integer ctime;

    private Integer mtime;

}