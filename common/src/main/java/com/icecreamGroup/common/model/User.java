package com.icecreamGroup.common.model;

import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "user")
public class User {
    private Integer id;

    private String name;

    private String password;
}