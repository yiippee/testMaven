package com.lzb.stream;

import lombok.Data;

@Data
public class OperationLogQO {
    private Integer skuId;
    private String sku;
    private String batchNo;
    private String attrName;
    private String newValue;
    public OperationLogQO(Integer skuId, String sku, String batchNo, String attrName, String newValue) {
        this.skuId = skuId;
        this.sku = sku;
        this.batchNo = batchNo;
        this.attrName = attrName;
        this.newValue = newValue;
    }
}
