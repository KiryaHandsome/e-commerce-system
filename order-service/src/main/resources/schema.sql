DROP TABLE IF EXISTS orders;

CREATE TABLE IF NOT EXISTS orders
(
    id               INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id      INTEGER        NOT NULL,
    product_count    INTEGER        NOT NULL CHECK ( product_count > 0 ),
    product_id       INTEGER        NOT NULL,
    total_price      NUMERIC(10, 2) NOT NULL,
    payment_status   VARCHAR        NOT NULL,
    inventory_status VARCHAR        NOT NULL,
    order_status     VARCHAR        NOT NULL
);
