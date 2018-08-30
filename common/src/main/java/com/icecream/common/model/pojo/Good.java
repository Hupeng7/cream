package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
public class Good implements Serializable {
    private Integer id;

    private Integer sid;

    private Integer isDigital;

    private Integer type;

    private Integer categoryId;

    private String specGroup;

    private String goodsSn;

    private Integer isHot;

    private Integer isBest;

    private Integer isNew;

    private Integer isPromote;

    private String goodsName;

    private String goodsIco;

    private String smallUrl;

    private String carouselUrl;

    private String videoUrl;

    private String pageUrl;

    private Integer isSale;

    private Integer onsaleTime;

    private Integer offsaleTime;

    private Integer onflashsaleTime;

    private Integer offflashsaleTime;

    private Integer dispalyflashsaleTime;

    private Integer dispalyflashsaleCountdownTime;

    private Integer sellNum;

    private BigDecimal totalSalePrice;

    private Integer goodsNum;

    private Integer warnNum;

    private BigDecimal goodsMarketPrice;

    private BigDecimal goodsPrice;

    private Integer isShowGoodsPrice;

    private BigDecimal freightPrice;

    private Integer freightTemplateId;

    private Integer buylimit;

    private String goodsIntroductUrl;

    private String goodsInstructions;

    private String activitiesAddress;

    private Integer onactivitiesTime;

    private Integer offactivitiesTime;

    private Byte status;

    private Integer provideInvoice;

    private Integer provideWarranty;

    private BigDecimal goodsWeight;

    private Integer clickCount;

    private Integer browseCount;

    private Integer shareCount;

    private Integer favoriteCount;

    private Integer likeCount;

    private Integer tenantId;

    private Integer score;

    private Integer isCheck;

    private Integer logisticsTemplateId;

    private Integer commentsId;

    private Integer shareId;

    private Integer qaId;

    private Integer sellmonthlyNum;

    private BigDecimal promotePrice;

    private Integer onpromoteTime;

    private Integer offpromoteTime;

    private BigDecimal rmbPrice;

    private BigDecimal dollarPrice;

    private Integer importDigitalCardTotal;

    private String keywords;

    private String note;

    private Integer isSynced;

    private Integer syncedTime;

    private Integer channelId;

    private Integer channelHId;

    private Integer ctime;

    private Integer mtime;

    private String goodsIntroduct;

    private String goodsDetail;

    private String channelHData;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    @Transient
    private List<GoodsSpec> goodsSpec;

}