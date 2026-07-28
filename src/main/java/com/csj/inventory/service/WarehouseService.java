package com.csj.inventory.service;

import com.csj.inventory.dto.request.WarehouseCreateRequest;
import com.csj.inventory.dto.request.WarehouseUpdateRequest;
import com.csj.inventory.dto.response.WarehouseInventoryResponse;
import com.csj.inventory.dto.response.WarehouseResponse;

import java.util.List;

public interface WarehouseService {

    WarehouseResponse create(WarehouseCreateRequest request);

    WarehouseResponse update(Long id, WarehouseUpdateRequest request);

    void delete(Long id);

    WarehouseResponse getById(Long id);

    List<WarehouseResponse> getAll();

    List<WarehouseInventoryResponse> getInventory(Long warehouseId);
}
