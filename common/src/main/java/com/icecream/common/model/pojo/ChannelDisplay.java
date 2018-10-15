package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
 * @date: 11:54 2018/9/10 0010
 */
@Data
@Table(name = "channel_display")
public class ChannelDisplay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer sid;

    private String name;

    private String remark;

    private Integer isDel;

    private Integer channelId;

    private Integer score;

    private Integer ctime;

    private Integer mtime;

    private String anth;

    private Integer permission;


}
