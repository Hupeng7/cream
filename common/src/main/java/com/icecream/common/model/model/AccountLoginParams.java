package com.icecream.common.model.model;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.NotBlank;


/**
 * @author Mr_h
 * @version 2.0
 * description:
 * create by Mr_h on 2018/7/6 0006
 */
@Data
public class AccountLoginParams{

    private String account;

    private String password;

    private Integer type;

}
