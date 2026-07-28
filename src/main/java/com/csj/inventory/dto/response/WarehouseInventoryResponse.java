package com.csj.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class WarehouseInventoryResponse implements Serializable {
    private Long warehouseId;
    private String warehouseCode;
    private Long productId;
    private String sku;
    private String productName;
    private Integer quantity;
    private LocalDateTime updatedAt;
}
