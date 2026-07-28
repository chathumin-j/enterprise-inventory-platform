CREATE TABLE inventory_transactions (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id         BIGINT NOT NULL,
    warehouse_id       BIGINT NOT NULL,
    related_warehouse_id BIGINT,
    action_type        VARCHAR(20) NOT NULL,
    quantity_changed   INT NOT NULL,
    previous_quantity  INT NOT NULL,
    new_quantity       INT NOT NULL,
    performed_by       VARCHAR(60) NOT NULL,
    notes              VARCHAR(500),
    created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_it_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_it_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id) ON DELETE CASCADE,
    CONSTRAINT fk_it_related_warehouse FOREIGN KEY (related_warehouse_id) REFERENCES warehouses (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_it_product ON inventory_transactions (product_id);
CREATE INDEX idx_it_warehouse ON inventory_transactions (warehouse_id);
CREATE INDEX idx_it_created_at ON inventory_transactions (created_at);
