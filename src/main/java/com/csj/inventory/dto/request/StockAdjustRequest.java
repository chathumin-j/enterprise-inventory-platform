package com.csj.inventory.dto.request;

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
public class StockAdjustRequest {

    @NotNull(message = "productId is required")
    private Long productId;

    @NotNull(message = "warehouseId is required")
    private Long warehouseId;

    /** Signed delta applied to current stock, e.g. -5 for a shrinkage write-off, +5 for a correction. */
    @NotNull(message = "quantityDelta is required")
    private Integer quantityDelta;

    @Size(max = 500)
    private String notes;
}
