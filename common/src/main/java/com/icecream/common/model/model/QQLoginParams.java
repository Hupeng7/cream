package com.icecream.common.model.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @version 2.0
 */
@Data
public class QQLoginParams extends LoginBaseParams{

    private String openId;

    private String accessToken;

    private Integer type;

}
