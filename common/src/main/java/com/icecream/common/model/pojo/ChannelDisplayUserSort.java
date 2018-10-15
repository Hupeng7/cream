package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
 * @date: 14:23 2018/9/10 0010
 */
@Data
@Table(name = "channel_display_user_sort")
public class ChannelDisplayUserSort {
    @Id
    private String id;

    private Integer uid;

    private String channelDisplays;

    private Integer ctime;

    private Integer mtime;


}
