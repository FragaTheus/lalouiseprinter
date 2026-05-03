CREATE TABLE restaurants
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(80)  NOT NULL,
    cnpj       VARCHAR(14)  NOT NULL UNIQUE,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);
