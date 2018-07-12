package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Table(name = "good_store")
public class GoodStore {

    @Id
    @GeneratedValue(generator = "UUID")
    private String id;

    private Integer goodNum;

    private Integer warnNum;

    private String goodId;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;

}