package com.icecream.common.model.requstbody;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @version 2.0
 */
@Data
public class QQLoginParams extends LoginBaseParams{

    @NotBlank(message = "QQ登录时，openId不能为空")
    private String openId;

    @NotBlank(message ="QQ登录时，access_token不能为空")
    private String accessToken;

    private Integer type;

}
