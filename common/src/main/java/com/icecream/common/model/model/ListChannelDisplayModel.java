package com.icecream.common.model.model;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
 * @date: 13:31 2018/9/10 0010
 */
@Data
public class ListChannelDisplayModel {
    @Range(min = 0, message = "sid不能小于0")
    @NotNull(message = "sid不能为空")
    private Integer sid;

    @NotNull(message = "lastUpdateTime不能为空")
    private Integer lastUpdateTime;

    private String channelDisplays;

}
