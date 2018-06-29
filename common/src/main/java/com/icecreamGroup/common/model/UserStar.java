package com.icecreamGroup.common.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "user_star")
public class UserStar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer isstar;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private Integer driver;

    private String smallavatar;

    private String avatar;

    private Integer masterChannelId;

    private Integer ctime;

    private Integer mtime;

    private String lang;

    private String timezone;

    private String phonetype;

    private Integer phonemodel;

    private Integer role;

    private Integer status;

    private Integer identity;

    private Integer birthday;

    private Integer sex;

    private Integer isdel;

    private String regIp;

    private String signature;

    private Integer conCheck;

    private Integer totalCheck;

    private Float exp;

    private Float score;

    private Float score1;

    private Float score2;

    private Float score3;

    private Float score4;

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