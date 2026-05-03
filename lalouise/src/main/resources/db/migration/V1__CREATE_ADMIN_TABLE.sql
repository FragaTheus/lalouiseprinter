CREATE TABLE admins
(
    id         UUID PRIMARY KEY,
    nickname   VARCHAR(30)  NOT NULL,
    email      VARCHAR(254) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    active     BOOLEAN      NOT NULL,
    created_at TIMESTAMP NOT NULL ,
    updated_at TIMESTAMP NOT NULL
);