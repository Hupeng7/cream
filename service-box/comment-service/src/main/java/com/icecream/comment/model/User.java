package com.icecream.comment.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/9/7 0007
 */
@Data
public class User implements Serializable {

    private String name;

    private Integer age;

    private List<Address> addressList;
}
