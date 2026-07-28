package com.csj.inventory.repository;

import com.csj.inventory.entity.WarehouseInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface WarehouseInventoryRepository extends JpaRepository<WarehouseInventory, Long> {

    Optional<WarehouseInventory> findByWarehouseIdAndProductId(Long warehouseId, Long productId);

    List<WarehouseInventory> findByWarehouseId(Long warehouseId);

    List<WarehouseInventory> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select wi from WarehouseInventory wi where wi.warehouse.id = :warehouseId and wi.product.id = :productId")
    Optional<WarehouseInventory> lockByWarehouseIdAndProductId(
            @Param("warehouseId") Long warehouseId, @Param("productId") Long productId);
}
