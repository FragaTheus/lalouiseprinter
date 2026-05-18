CREATE TABLE credentials
(
    id            UUID         PRIMARY KEY,
    role          VARCHAR(20)  NOT NULL,
    nickname      VARCHAR(30)  NOT NULL,
    email         VARCHAR(254) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    restaurant_id UUID,
    sector_id     UUID,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_credentials_role
        CHECK (role IN ('ADMIN', 'MANAGER', 'STAFF')),

    -- ADMIN nunca tem restaurante vinculado
    CONSTRAINT chk_admin_no_restaurant
        CHECK (role != 'ADMIN' OR restaurant_id IS NULL),

    -- MANAGER e STAFF sempre têm restaurante
    CONSTRAINT chk_staff_has_restaurant
        CHECK (role = 'ADMIN' OR restaurant_id IS NOT NULL)
);

CREATE INDEX idx_credentials_email         ON credentials (email);
CREATE INDEX idx_credentials_role          ON credentials (role);
CREATE INDEX idx_credentials_restaurant_id ON credentials (restaurant_id);

