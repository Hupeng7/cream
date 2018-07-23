package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author Mr_h
 * @version 1.0
 * description：版主端权限表
 * create by Mr_h on 2018/7/23 0023
 */
@Data
@Table(name = "user_permission")
public class UserPermission {

    @Id
    @GeneratedValue(generator = "UUID")
    private String id;

    private String permissionName;

    private String permissionUrl;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
