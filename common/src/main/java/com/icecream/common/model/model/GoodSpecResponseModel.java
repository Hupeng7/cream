package com.icecream.common.model.model;

import lombok.Data;

import java.util.List;

@Data
public class GoodSpecResponseModel {
    private String specName;

    private List<String> specList;
}
