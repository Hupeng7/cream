package com.icecream.common.model.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
 * @date: 11:25 2018/9/5 0005
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysPhotoFrameAndUserInfo {
    @Id
    private String id;

    private String name;

    private String groupName;

    private String img;

    private BigDecimal price;

    private Integer level;

    private Integer term;

    private Integer endTime;

    private Integer isWear;

    private String endTimeNote;

}
