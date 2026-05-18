CREATE TABLE sectors
(
    id             UUID         PRIMARY KEY,
    name           VARCHAR(30)  NOT NULL,
    description    VARCHAR(255) NOT NULL,
    restaurant_id  UUID         NOT NULL,
    responsible_id UUID,
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_sector_name_restaurant UNIQUE (name, restaurant_id)
);

CREATE TABLE sector_storages
(
    sector_id UUID        NOT NULL,
    storage   VARCHAR(20) NOT NULL,
    CONSTRAINT fk_sector_storages_sector FOREIGN KEY (sector_id) REFERENCES sectors (id) ON DELETE CASCADE,
    CONSTRAINT chk_storage CHECK (storage IN ('AMBIENT', 'REFRIGERATED', 'FROZEN', 'DEEP_FROZEN'))
);

