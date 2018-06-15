package com.icecreamGroup.common.model;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nickname;

    private String smallavatar;

    private String avatar;

    private Integer driver;

    private String authid;

    private String itucode;

    private String phone;

    private String phonetype;

    private Integer phonemodel;

    private String email;

    private String uname;

    private Integer role;

    private Integer status;

    private Integer identity;

    private Integer birthday;

    private Integer sex;

    private String professional;

    private String address;

    private Integer addressid;

    private Integer isdel;

    private Integer tag;

    private String regIp;

    private String openid;

    private String signature;

    private String inviteCode;

    private Integer conCheck;

    private Integer totalCheck;

    private Float exp;

    private Integer expid;

    private Float score;

    private Integer scoreid;

    private Float score1;

    private Float score2;

    private Float score3;

    private Float score4;

    private Integer ctime;

    private Integer mtime;

    private String lang;

    private Integer timezone;

    private Integer lastlogintime;

    private String lastloginip;

    private String searchKey;

    private Integer topicCount;

    private Integer replyCount;

    private Integer followerCount;

    private Integer followingCount;

    private Integer commentCount;

    private Integer friendCount;

    private Integer forwardCount;


}