CREATE TABLE products
(
    id            UUID          PRIMARY KEY,
    name          VARCHAR(30)   NOT NULL,
    category      VARCHAR(20)   NOT NULL DEFAULT 'PROTEIN',
    restaurant_id UUID          NOT NULL,
    active        BOOLEAN       NOT NULL,
    created_at    TIMESTAMP     NOT NULL,
    updated_at    TIMESTAMP     NOT NULL,
    CONSTRAINT uq_product_name_restaurant UNIQUE (name, restaurant_id),
    CONSTRAINT chk_product_category CHECK (category IN ('PROTEIN', 'SEAFOOD', 'VEGETABLE', 'GRAINS', 'PASTA', 'SEASONINGS', 'SAUCES', 'OILS'))
);

