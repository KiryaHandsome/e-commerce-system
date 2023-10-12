DELETE
FROM balances
WHERE true;

INSERT INTO balances(customer_id, balance)
VALUES (3, 888.90);