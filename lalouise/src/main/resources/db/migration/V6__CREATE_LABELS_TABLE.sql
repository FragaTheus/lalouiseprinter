CREATE TABLE labels
(
    id            UUID        PRIMARY KEY,
    restaurant_id UUID        NOT NULL,
    sector_id     UUID        NOT NULL,
    product_id    UUID        NOT NULL,
    user_id       UUID        NOT NULL,
    lot           VARCHAR(10) NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    validate_date TIMESTAMP   NOT NULL,
    created_at    TIMESTAMP   NOT NULL,
    updated_at    TIMESTAMP   NOT NULL,
    CONSTRAINT fk_label_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
    CONSTRAINT fk_label_sector     FOREIGN KEY (sector_id)     REFERENCES sectors (id),
    CONSTRAINT fk_label_product    FOREIGN KEY (product_id)    REFERENCES products (id),
    -- user_id referencia credentials (tabela unificada)
    CONSTRAINT fk_label_user       FOREIGN KEY (user_id)       REFERENCES credentials (id),
    CONSTRAINT chk_label_status    CHECK (status IN ('ACTIVE', 'EXPIRING', 'EXPIRED', 'DISCARDED'))
);

CREATE INDEX idx_labels_restaurant_id ON labels (restaurant_id);
CREATE INDEX idx_labels_sector_id     ON labels (sector_id);
CREATE INDEX idx_labels_lot           ON labels (lot);
CREATE INDEX idx_labels_status        ON labels (status);

