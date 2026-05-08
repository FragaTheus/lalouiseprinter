CREATE TABLE products
(
    id            UUID         PRIMARY KEY,
    name          VARCHAR(30)  NOT NULL,
    description   VARCHAR(255) NOT NULL,
    restaurant_id UUID         NOT NULL,
    active        BOOLEAN      NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL,
    CONSTRAINT uq_product_name_restaurant UNIQUE (name, restaurant_id)
);

