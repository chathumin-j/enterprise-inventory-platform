package com.csj.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class InventoryTransactionResponse {
    private Long id;
    private Long productId;
    private String sku;
    private Long warehouseId;
    private String warehouseCode;
    private Long relatedWarehouseId;
    private String relatedWarehouseCode;
    private String actionType;
    private Integer quantityChanged;
    private Integer previousQuantity;
    private Integer newQuantity;
    private String performedBy;
    private String notes;
    private LocalDateTime createdAt;
}
