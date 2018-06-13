package com.icecreamGroup.common.model;

import lombok.Data;

/**
 * @author: Mr_h
 * {@link}
 * 描述: token被解密之后的实体类
 * create by 2018/6/8 0008
 */
@Data
public class TokenEntity {

     //用户id
     private String uid;

     //时间戳
     private String timeStamp;

     //角色
     private String role;

}

