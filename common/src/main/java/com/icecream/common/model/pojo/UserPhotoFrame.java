package com.icecream.common.model.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
 * @date: 17:01 2018/9/4 0004
 */
@Data
@Table(name = "user_photo_frame")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPhotoFrame {
    @Id
    private String id;

    private Integer uid;

    private String frameId;

    private String frameImg;

    private Integer ctime;

    private Integer endTime;

    private Integer isWear;

    private Short IsInuse;


}
