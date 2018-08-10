package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;


@Data
@Table(name = "score_rule")
public class ScoreRule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer sid;

    private Integer categoryId;

    private String code;

    private String dispalyName;

    private BigDecimal rechargePrice;

    private BigDecimal price;

    private Integer status;

    private String note;

    private Integer score;

    private Integer ctime;

    private Integer mtime;

    private Integer channelReplyLimit;

    private Integer channelReplyLimitCount;
}