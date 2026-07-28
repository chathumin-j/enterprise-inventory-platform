package com.csj.inventory.service;

import com.csj.inventory.dto.request.StockAdjustRequest;
import com.csj.inventory.dto.request.StockReceiveRequest;
import com.csj.inventory.dto.request.StockTransferRequest;
import com.csj.inventory.dto.response.InventoryTransactionResponse;
import com.csj.inventory.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface InventoryService {

    InventoryTransactionResponse receiveStock(StockReceiveRequest request);

    InventoryTransactionResponse adjustStock(StockAdjustRequest request);

    InventoryTransactionResponse[] transferStock(StockTransferRequest request);

    PageResponse<InventoryTransactionResponse> getHistory(Pageable pageable);

    PageResponse<InventoryTransactionResponse> getHistoryForProduct(Long productId, Pageable pageable);
}
