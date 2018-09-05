package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
 * @date: 16:56 2018/9/4 0004
 */
@Data
@Table(name = "sys_photo_frame")
public class SysPhotoFrame {
    @Id
    private String id;

    private String name;

    private String groupName;

    private String img;

    private BigDecimal price;

    private Integer level;

    private Integer term;

    private Integer sort;

    private Integer saleNum;

    private Integer ctime;

    private Short isInuse;


}
