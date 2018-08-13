package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(name = "appkeys")
public class Appkeys implements Serializable {

    private Integer id;

    private String appid;

    private String appsecret;

    private String remark;

}