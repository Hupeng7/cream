package com.icecream.common.model.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
 * @date: 10:43 2018/9/6 0006
 */
@Data
public class CreateSysPhotoFrameModel {
    @NotBlank(message = "系统头像框名称不能为空")
    private String name;

    @NotBlank(message = "系统头像框分组名称不能为空")
    private String groupName;

    @NotBlank(message = "系统头像框url不能为空")
    private String img;

    @Range(min = 0, message = "价格不能小于0")
    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @Range(min = 0, message = "等级不能小于0")
    @NotNull(message = "等级不能为空")
    private Integer level;


}
