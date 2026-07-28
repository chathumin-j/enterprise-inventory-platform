package com.csj.inventory.controller;

import com.csj.inventory.dto.request.StockAdjustRequest;
import com.csj.inventory.dto.request.StockReceiveRequest;
import com.csj.inventory.dto.request.StockTransferRequest;
import com.csj.inventory.dto.response.ApiResponse;
import com.csj.inventory.dto.response.InventoryTransactionResponse;
import com.csj.inventory.dto.response.PageResponse;
import com.csj.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Stock receiving, adjustment, transfer and history")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Receive new stock into a warehouse")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    @PostMapping(value = "/receive", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<InventoryTransactionResponse>> receive(@Valid @RequestBody StockReceiveRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Stock received", inventoryService.receiveStock(request)));
    }

    @Operation(summary = "Adjust stock levels (correction / shrinkage / damage write-off)")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping(value = "/adjust", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<InventoryTransactionResponse>> adjust(@Valid @RequestBody StockAdjustRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Stock adjusted", inventoryService.adjustStock(request)));
    }

    @Operation(summary = "Transfer stock between two warehouses")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping(value = "/transfer", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<InventoryTransactionResponse[]>> transfer(@Valid @RequestBody StockTransferRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Stock transferred", inventoryService.transferStock(request)));
    }

    @Operation(summary = "View full inventory transaction history")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    @GetMapping(value = "/history", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<PageResponse<InventoryTransactionResponse>>> history(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getHistory(pageable)));
    }

    @Operation(summary = "View inventory transaction history for a specific product")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    @GetMapping(value = "/history/product/{productId}", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<PageResponse<InventoryTransactionResponse>>> historyForProduct(
            @PathVariable Long productId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getHistoryForProduct(productId, pageable)));
    }
}
