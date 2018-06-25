package com.icecreamGroup.common.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "user")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String nickname;

    private String smallavatar;

    private String avatar;

    private Integer driver;

    private Integer role;

    private Integer status;

    private Integer sex;

    private Integer birthday;

    private Integer exp;

    private Integer expid;

    private Integer scoreid;

    private String authid;

    private String itucode;

    private String phone;

    private String phonetype;

    private Integer phonemodel;

    private String email;

    private String uname;

    private Integer identity;

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

    private Integer score;

    private Integer score1;

    private Integer score2;

    private Integer score3;

    private Integer score4;

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

    @Transient
    private Integer registerType;
    @Transient
    private String register;
    @Transient
    private String password;
}