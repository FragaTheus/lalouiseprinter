CREATE TABLE sectors
(
    id            UUID PRIMARY KEY,
    name          VARCHAR(30) NOT NULL,
    restaurant_id UUID        NOT NULL,
    responsible_id UUID,
    created_at    TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP   NOT NULL DEFAULT NOW()
);

