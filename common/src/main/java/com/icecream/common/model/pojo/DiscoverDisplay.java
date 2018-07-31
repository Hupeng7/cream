package com.icecream.common.model.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.icecream.common.util.time.DateUtil;
import lombok.Data;

import java.util.Date;


@Data
public class DiscoverDisplay {
    private Integer id;

    private Integer sid;

    private String name;

    private String remark;

    private Integer isdel;

    private Integer discoverid;

    private Integer score;

    private Integer ctime;

    private Integer mtime;
}