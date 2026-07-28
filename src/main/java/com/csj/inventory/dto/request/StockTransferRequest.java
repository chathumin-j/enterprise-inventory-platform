package com.csj.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockTransferRequest {

    @NotNull(message = "productId is required")
    private Long productId;

    @NotNull(message = "sourceWarehouseId is required")
    private Long sourceWarehouseId;

    @NotNull(message = "destinationWarehouseId is required")
    private Long destinationWarehouseId;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity transferred must be at least 1")
    private Integer quantity;

    @Size(max = 500)
    private String notes;
}
