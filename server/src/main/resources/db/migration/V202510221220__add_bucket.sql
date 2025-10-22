CREATE TABLE bucket
(
    id         BIGINT PRIMARY KEY,
    state      BLOB,
    expires_at BIGINT
);
