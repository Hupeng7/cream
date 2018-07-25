package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class Good {

    private String id;

    private Integer sid;

    private Integer isDigital;

    private Integer type;

    private String goodsSn;

    private Integer isHot;

    private Integer isBest;

    private Integer isNew;

    private String goodsName;

    private String goodsIco;

    private String smallUrl;

    private String carouselUrl;

    private String videoUrl;

    private String pageUrl;

    private Integer isSale;

    private Integer dispalyflashsaleTime;

    private Integer dispalyflashsaleCountdownTime;

    private BigDecimal goodsPrice;

    private Integer isShowGoodsPrice;

    private Integer buylimit;

    private String goodsIntroductUrl;

    private String goodsInstructions;

    private String activitiesAddress;

    private Integer status;

    private BigDecimal goodsWeight;

    private Integer score;

    private Integer isCheck;

    private Integer importDigitalCardTotal;

    private String keywords;

    private String note;

    private String itemNo;

    private String specGroup;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime onactivitiesTime;

    private LocalDateTime offactivitiesTime;

    private LocalDateTime onsaleTime;

    private LocalDateTime offsaleTime;

    private LocalDateTime onflashsaleTime;

    private LocalDateTime offflashsaleTime;

    private String goodsIntroduct;

    private String goodsDetail;

    @Transient
    private List<GoodsSpec> goodsSpec;

}