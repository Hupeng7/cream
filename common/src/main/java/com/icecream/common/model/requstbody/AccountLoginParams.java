package com.icecream.common.model.requstbody;
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

    @NonNull
    @NotBlank(message = "密码不能为空")
    private String account;

    @NonNull
    @NotBlank(message = "密码不能为空")
    private String password;

    private Integer type;

}
