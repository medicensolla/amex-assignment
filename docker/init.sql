CREATE TABLE IF NOT EXISTS items (
                                     id SERIAL PRIMARY KEY,
                                     description VARCHAR(255) NOT NULL,
    cost DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1
    );

INSERT INTO items (description, cost, quantity) VALUES
                                                    ('Apple', 0.60, 10),
                                                    ('Orange', 0.25, 15);