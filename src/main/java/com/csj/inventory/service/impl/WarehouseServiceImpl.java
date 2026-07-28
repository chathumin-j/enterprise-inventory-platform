package com.csj.inventory.service.impl;

import com.csj.inventory.dto.mapper.WarehouseMapper;
import com.csj.inventory.dto.request.WarehouseCreateRequest;
import com.csj.inventory.dto.request.WarehouseUpdateRequest;
import com.csj.inventory.dto.response.WarehouseInventoryResponse;
import com.csj.inventory.dto.response.WarehouseResponse;
import com.csj.inventory.entity.Warehouse;
import com.csj.inventory.exception.DuplicateResourceException;
import com.csj.inventory.exception.ResourceNotFoundException;
import com.csj.inventory.logging.Loggable;
import com.csj.inventory.repository.WarehouseInventoryRepository;
import com.csj.inventory.repository.WarehouseRepository;
import com.csj.inventory.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    @Override
    @Transactional
    @Loggable(action = "CREATE_WAREHOUSE", entity = "Warehouse")
    public WarehouseResponse create(WarehouseCreateRequest request) {
        if (warehouseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Warehouse code already exists: " + request.getCode());
        }
        Warehouse saved = warehouseRepository.save(WarehouseMapper.toEntity(request));
        return WarehouseMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Loggable(action = "UPDATE_WAREHOUSE", entity = "Warehouse")
    public WarehouseResponse update(Long id, WarehouseUpdateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Warehouse", id));
        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        return WarehouseMapper.toResponse(warehouseRepository.save(warehouse));
    }

    @Override
    @Transactional
    @Loggable(action = "DELETE_WAREHOUSE", entity = "Warehouse")
    public void delete(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw ResourceNotFoundException.of("Warehouse", id);
        }
        warehouseRepository.deleteById(id);
    }

    @Override
    public WarehouseResponse getById(Long id) {
        return warehouseRepository.findById(id)
                .map(WarehouseMapper::toResponse)
                .orElseThrow(() -> ResourceNotFoundException.of("Warehouse", id));
    }

    @Override
    public List<WarehouseResponse> getAll() {
        return warehouseRepository.findAll().stream()
                .map(WarehouseMapper::toResponse)
                .toList();
    }

    @Override
    public List<WarehouseInventoryResponse> getInventory(Long warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw ResourceNotFoundException.of("Warehouse", warehouseId);
        }
        return warehouseInventoryRepository.findByWarehouseId(warehouseId).stream()
                .map(WarehouseMapper::toInventoryResponse)
                .toList();
    }
}
