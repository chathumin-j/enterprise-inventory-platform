package com.csj.inventory.controller;

import com.csj.inventory.dto.request.WarehouseCreateRequest;
import com.csj.inventory.dto.request.WarehouseUpdateRequest;
import com.csj.inventory.dto.response.ApiResponse;
import com.csj.inventory.dto.response.WarehouseInventoryResponse;
import com.csj.inventory.dto.response.WarehouseResponse;
import com.csj.inventory.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@Tag(name = "Warehouses", description = "Warehouse management")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Operation(summary = "Create a new warehouse")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping(value = "/", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<WarehouseResponse>> create(@Valid @RequestBody WarehouseCreateRequest request) {
        WarehouseResponse response = warehouseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Warehouse created", response));
    }

    @Operation(summary = "Update an existing warehouse")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping(value = "/{id}", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<WarehouseResponse>> update(@PathVariable Long id,
                                                                    @Valid @RequestBody WarehouseUpdateRequest request) {
        WarehouseResponse response = warehouseService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Warehouse updated", response));
    }

    @Operation(summary = "Delete a warehouse")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Warehouse deleted", null));
    }

    @Operation(summary = "Get a warehouse by id")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    @GetMapping(value = "/{id}", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(warehouseService.getById(id)));
    }

    @Operation(summary = "List all warehouses")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    @GetMapping(value = "/", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<List<WarehouseResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(warehouseService.getAll()));
    }

    @Operation(summary = "View current inventory levels for a warehouse")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    @GetMapping(value = "/{id}/inventory", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<List<WarehouseInventoryResponse>>> getInventory(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(warehouseService.getInventory(id)));
    }
}
