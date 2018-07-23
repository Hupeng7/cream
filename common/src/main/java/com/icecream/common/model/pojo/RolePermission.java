package com.icecream.common.model.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author Mr_h
 * @version 1.0
 * description: 角色权限表
 * create by Mr_h on 2018/7/23 0023
 */
@Data
@Table(name = "role_permission")
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer roleId;

    private String permissionId;

    private Integer isUsed;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
