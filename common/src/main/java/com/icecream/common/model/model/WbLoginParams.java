package com.icecream.common.model.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;


/**
 * @version 2.0
 */
@Data
public class WbLoginParams extends LoginBaseParams{

    @JSONField(name="uid")
    private String openId;

    private String accessToken;

    private Integer type;
}
