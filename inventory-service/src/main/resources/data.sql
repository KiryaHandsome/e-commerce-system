DELETE
FROM inventories
WHERE true;

INSERT INTO inventories(product_id, product_count)
VALUES (1, 4),
       (2, 100),
       (3, 0),
       (4, 33),
       (5, 1),
       (6, 6);