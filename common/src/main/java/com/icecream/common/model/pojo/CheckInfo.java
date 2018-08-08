package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.Table;
import javax.persistence.Transient;

@Data
@Table(name = "check_info")
public class CheckInfo {

    private Integer id;

    private Integer sid;

    private Integer uid;

    private Integer conNum;

    private Integer totalNum;

    private Integer checkTime;

    private Integer ctime;

    private Integer mtime;

}