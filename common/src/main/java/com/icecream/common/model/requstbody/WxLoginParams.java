package com.icecream.common.model.requstbody;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @version 2.0
 */
@Data
public class WxLoginParams extends LoginBaseParams{

    @NotBlank(message = "微信登录code不能为空")
    private String code;

    private Integer type;

}
