package com.csj.inventory.service.impl;

import com.csj.inventory.dto.mapper.InventoryMapper;
import com.csj.inventory.dto.request.StockAdjustRequest;
import com.csj.inventory.dto.request.StockReceiveRequest;
import com.csj.inventory.dto.request.StockTransferRequest;
import com.csj.inventory.dto.response.InventoryTransactionResponse;
import com.csj.inventory.dto.response.PageResponse;
import com.csj.inventory.entity.*;
import com.csj.inventory.exception.InsufficientStockException;
import com.csj.inventory.exception.ResourceNotFoundException;
import com.csj.inventory.logging.Loggable;
import com.csj.inventory.repository.InventoryTransactionRepository;
import com.csj.inventory.repository.ProductRepository;
import com.csj.inventory.repository.WarehouseInventoryRepository;
import com.csj.inventory.repository.WarehouseRepository;
import com.csj.inventory.service.CacheService;
import com.csj.inventory.service.InventoryService;
import com.csj.inventory.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private static final String PRODUCT_KEY_PREFIX = "product:";
    private static final String PRODUCT_LIST_KEY_PREFIX = "product:list:";

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final CacheService cacheService;

    @Override
    @Transactional
    @Loggable(action = "STOCK_RECEIVE", entity = "InventoryTransaction")
    public InventoryTransactionResponse receiveStock(StockReceiveRequest request) {
        Product product = getProduct(request.getProductId());
        Warehouse warehouse = getWarehouse(request.getWarehouseId());
        WarehouseInventory line = getOrCreateLine(warehouse, product);

        int previous = line.getQuantity();
        int updated = previous + request.getQuantity();
        line.setQuantity(updated);
        warehouseInventoryRepository.save(line);

        product.setQuantity(product.getQuantity() + request.getQuantity());
        productRepository.save(product);

        InventoryTransaction tx = InventoryTransaction.builder()
                .product(product)
                .warehouse(warehouse)
                .actionType(InventoryActionType.RECEIVE)
                .quantityChanged(request.getQuantity())
                .previousQuantity(previous)
                .newQuantity(updated)
                .performedBy(SecurityUtils.getCurrentUsername())
                .notes(request.getNotes())
                .build();
        InventoryTransaction saved = inventoryTransactionRepository.save(tx);

        invalidateProductCache(product.getId());
        return InventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Loggable(action = "STOCK_ADJUST", entity = "InventoryTransaction")
    public InventoryTransactionResponse adjustStock(StockAdjustRequest request) {
        Product product = getProduct(request.getProductId());
        Warehouse warehouse = getWarehouse(request.getWarehouseId());
        WarehouseInventory line = getOrCreateLine(warehouse, product);

        int previous = line.getQuantity();
        int updated = previous + request.getQuantityDelta();
        if (updated < 0) {
            throw new InsufficientStockException(
                    "Adjustment would result in negative stock for product " + product.getSku()
                            + " at warehouse " + warehouse.getCode());
        }
        line.setQuantity(updated);
        warehouseInventoryRepository.save(line);

        int newProductTotal = product.getQuantity() + request.getQuantityDelta();
        product.setQuantity(Math.max(newProductTotal, 0));
        productRepository.save(product);

        InventoryTransaction tx = InventoryTransaction.builder()
                .product(product)
                .warehouse(warehouse)
                .actionType(InventoryActionType.ADJUST)
                .quantityChanged(request.getQuantityDelta())
                .previousQuantity(previous)
                .newQuantity(updated)
                .performedBy(SecurityUtils.getCurrentUsername())
                .notes(request.getNotes())
                .build();
        InventoryTransaction saved = inventoryTransactionRepository.save(tx);

        invalidateProductCache(product.getId());
        return InventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Loggable(action = "STOCK_TRANSFER", entity = "InventoryTransaction")
    public InventoryTransactionResponse[] transferStock(StockTransferRequest request) {
        if (request.getSourceWarehouseId().equals(request.getDestinationWarehouseId())) {
            throw new IllegalArgumentException("Source and destination warehouse must be different");
        }

        Product product = getProduct(request.getProductId());
        Warehouse source = getWarehouse(request.getSourceWarehouseId());
        Warehouse destination = getWarehouse(request.getDestinationWarehouseId());

        WarehouseInventory sourceLine = getOrCreateLine(source, product);
        int sourcePrevious = sourceLine.getQuantity();
        if (sourcePrevious < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock of " + product.getSku() + " at warehouse " + source.getCode()
                            + " (available: " + sourcePrevious + ", requested: " + request.getQuantity() + ")");
        }
        int sourceUpdated = sourcePrevious - request.getQuantity();
        sourceLine.setQuantity(sourceUpdated);
        warehouseInventoryRepository.save(sourceLine);

        WarehouseInventory destinationLine = getOrCreateLine(destination, product);
        int destinationPrevious = destinationLine.getQuantity();
        int destinationUpdated = destinationPrevious + request.getQuantity();
        destinationLine.setQuantity(destinationUpdated);
        warehouseInventoryRepository.save(destinationLine);

        InventoryTransaction outTx = InventoryTransaction.builder()
                .product(product)
                .warehouse(source)
                .relatedWarehouse(destination)
                .actionType(InventoryActionType.TRANSFER_OUT)
                .quantityChanged(-request.getQuantity())
                .previousQuantity(sourcePrevious)
                .newQuantity(sourceUpdated)
                .performedBy(SecurityUtils.getCurrentUsername())
                .notes(request.getNotes())
                .build();
        InventoryTransaction savedOut = inventoryTransactionRepository.save(outTx);

        InventoryTransaction inTx = InventoryTransaction.builder()
                .product(product)
                .warehouse(destination)
                .relatedWarehouse(source)
                .actionType(InventoryActionType.TRANSFER_IN)
                .quantityChanged(request.getQuantity())
                .previousQuantity(destinationPrevious)
                .newQuantity(destinationUpdated)
                .performedBy(SecurityUtils.getCurrentUsername())
                .notes(request.getNotes())
                .build();
        InventoryTransaction savedIn = inventoryTransactionRepository.save(inTx);

        invalidateProductCache(product.getId());
        return new InventoryTransactionResponse[]{InventoryMapper.toResponse(savedOut), InventoryMapper.toResponse(savedIn)};
    }

    @Override
    public PageResponse<InventoryTransactionResponse> getHistory(Pageable pageable) {
        Page<InventoryTransaction> page = inventoryTransactionRepository.findAllByOrderByCreatedAtDesc(pageable);
        return PageResponse.from(page.map(InventoryMapper::toResponse));
    }

    @Override
    public PageResponse<InventoryTransactionResponse> getHistoryForProduct(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw ResourceNotFoundException.of("Product", productId);
        }
        Page<InventoryTransaction> page =
                inventoryTransactionRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
        return PageResponse.from(page.map(InventoryMapper::toResponse));
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", id));
    }

    private Warehouse getWarehouse(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Warehouse", id));
    }

    /**
     * Retrieves the warehouse/product stock line under a pessimistic write
     * lock, creating an initial zero-quantity line first if none exists yet.
     */
    private WarehouseInventory getOrCreateLine(Warehouse warehouse, Product product) {
        return warehouseInventoryRepository.lockByWarehouseIdAndProductId(warehouse.getId(), product.getId())
                .orElseGet(() -> {
                    WarehouseInventory created = WarehouseInventory.builder()
                            .warehouse(warehouse)
                            .product(product)
                            .quantity(0)
                            .build();
                    warehouseInventoryRepository.saveAndFlush(created);
                    return warehouseInventoryRepository
                            .lockByWarehouseIdAndProductId(warehouse.getId(), product.getId())
                            .orElseThrow(() -> new IllegalStateException("Failed to initialize stock line"));
                });
    }

    private void invalidateProductCache(Long productId) {
        cacheService.evict(PRODUCT_KEY_PREFIX + productId, "Product");
        cacheService.evictByPrefix(PRODUCT_LIST_KEY_PREFIX, "Product");
    }
}
