package com.csj.inventory.dto.mapper;

import com.csj.inventory.dto.response.InventoryTransactionResponse;
import com.csj.inventory.entity.InventoryTransaction;

public final class InventoryMapper {

    private InventoryMapper() {
    }

    public static InventoryTransactionResponse toResponse(InventoryTransaction tx) {
        return InventoryTransactionResponse.builder()
                .id(tx.getId())
                .productId(tx.getProduct().getId())
                .sku(tx.getProduct().getSku())
                .warehouseId(tx.getWarehouse().getId())
                .warehouseCode(tx.getWarehouse().getCode())
                .relatedWarehouseId(tx.getRelatedWarehouse() != null ? tx.getRelatedWarehouse().getId() : null)
                .relatedWarehouseCode(tx.getRelatedWarehouse() != null ? tx.getRelatedWarehouse().getCode() : null)
                .actionType(tx.getActionType().name())
                .quantityChanged(tx.getQuantityChanged())
                .previousQuantity(tx.getPreviousQuantity())
                .newQuantity(tx.getNewQuantity())
                .performedBy(tx.getPerformedBy())
                .notes(tx.getNotes())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
