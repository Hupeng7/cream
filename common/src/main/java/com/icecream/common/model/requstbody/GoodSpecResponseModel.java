package com.icecream.common.model.requstbody;

import lombok.Data;

import java.util.List;

@Data
public class GoodSpecResponseModel {
    private String specName;

    private List<String> specList;
}
