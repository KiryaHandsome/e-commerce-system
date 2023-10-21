DROP TABLE IF EXISTS inventories;

CREATE TABLE IF NOT EXISTS inventories
(
    id            INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id    INTEGER UNIQUE NOT NULL,
    product_count INTEGER CHECK ( product_count >= 0 )
);