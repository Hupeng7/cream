package com.icecream.common.model.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;


/**
 * @version 2.0
 */
@Data
public class WbLoginParams extends LoginBaseParams{

    @NotBlank(message ="微博登录时，uid不能为空")
    @JSONField(name="uid")
    private String openId;


    @NotBlank(message = "accessToken不能为空")
    private String accessToken;

    private Integer type;
}
