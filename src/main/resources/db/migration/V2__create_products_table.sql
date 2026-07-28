CREATE TABLE products (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku         VARCHAR(50)     NOT NULL,
    name        VARCHAR(150)    NOT NULL,
    description VARCHAR(1000),
    price       DECIMAL(12,2)   NOT NULL,
    quantity    INT             NOT NULL DEFAULT 0,
    category    VARCHAR(80)     NOT NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_products_sku UNIQUE (sku)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_products_category ON products (category);
