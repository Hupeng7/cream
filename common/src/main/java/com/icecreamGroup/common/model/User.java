package com.icecreamGroup.common.model;

import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "user1")
public class User {
    private Integer id;

    private Integer uid;

    private String name;

    private String password;
}