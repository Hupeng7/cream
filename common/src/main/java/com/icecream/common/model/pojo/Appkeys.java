package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "appkeys")
public class Appkeys {
    private Integer id;

    private String appid;

    private String appsecret;

    private String remark;

}