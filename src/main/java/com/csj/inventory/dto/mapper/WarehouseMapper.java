package com.csj.inventory.dto.mapper;

import com.csj.inventory.dto.request.WarehouseCreateRequest;
import com.csj.inventory.dto.response.WarehouseInventoryResponse;
import com.csj.inventory.dto.response.WarehouseResponse;
import com.csj.inventory.entity.Warehouse;
import com.csj.inventory.entity.WarehouseInventory;

public final class WarehouseMapper {

    private WarehouseMapper() {
    }

    public static Warehouse toEntity(WarehouseCreateRequest request) {
        return Warehouse.builder()
                .code(request.getCode())
                .name(request.getName())
                .location(request.getLocation())
                .build();
    }

    public static WarehouseResponse toResponse(Warehouse warehouse) {
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .code(warehouse.getCode())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .createdAt(warehouse.getCreatedAt())
                .updatedAt(warehouse.getUpdatedAt())
                .build();
    }

    public static WarehouseInventoryResponse toInventoryResponse(WarehouseInventory inventory) {
        return WarehouseInventoryResponse.builder()
                .warehouseId(inventory.getWarehouse().getId())
                .warehouseCode(inventory.getWarehouse().getCode())
                .productId(inventory.getProduct().getId())
                .sku(inventory.getProduct().getSku())
                .productName(inventory.getProduct().getName())
                .quantity(inventory.getQuantity())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}
