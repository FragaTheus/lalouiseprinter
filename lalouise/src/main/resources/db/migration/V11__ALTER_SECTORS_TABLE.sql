ALTER TABLE sectors
    ADD COLUMN description VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN active      BOOLEAN      NOT NULL DEFAULT TRUE;

ALTER TABLE sectors
    ALTER COLUMN description DROP DEFAULT;

ALTER TABLE sectors
    ADD CONSTRAINT uq_sector_name_restaurant UNIQUE (name, restaurant_id);

CREATE TABLE sector_storages
(
    sector_id UUID         NOT NULL,
    storage   VARCHAR(20)  NOT NULL,
    CONSTRAINT fk_sector_storages_sector FOREIGN KEY (sector_id) REFERENCES sectors (id) ON DELETE CASCADE,
    CONSTRAINT chk_storage CHECK (storage IN ('AMBIENT', 'REFRIGERATED', 'FROZEN', 'DEEP_FROZEN'))
);

