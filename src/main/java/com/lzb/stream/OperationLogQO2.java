package com.lzb.stream;

import lombok.Data;

@Data
public class OperationLogQO2 {
    private Integer skuId;
    private String attrName;
    private String newValue;
    private Integer distributorID;
    private String details;

    public OperationLogQO2(Integer skuId, String attrName, String newValue) {
        this.skuId = skuId;
        this.attrName = attrName;
        this.newValue = newValue;
    }
}
