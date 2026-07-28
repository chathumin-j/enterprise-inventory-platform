CREATE TABLE warehouse_inventory (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id   BIGINT NOT NULL,
    product_id     BIGINT NOT NULL,
    quantity       INT    NOT NULL DEFAULT 0,
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_wi_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id) ON DELETE CASCADE,
    CONSTRAINT fk_wi_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT uk_warehouse_product UNIQUE (warehouse_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
