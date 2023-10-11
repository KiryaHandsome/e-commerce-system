CREATE TABLE inventories
(
    id            INTEGER GENERATED ALWAYS AS IDENTITY,
    product_id    INTEGER UNIQUE NOT NULL,
    product_count INTEGER CHECK ( product_count >= 0 )
);