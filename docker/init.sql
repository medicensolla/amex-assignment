
CREATE TABLE IF NOT EXISTS orders (
                                      id SERIAL PRIMARY KEY,
                                      final_cost DECIMAL(10, 2) NOT NULL
    );
CREATE TABLE IF NOT EXISTS items (
                                     id SERIAL PRIMARY KEY,
                                     description VARCHAR(255) NOT NULL,
    cost DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    order_id INT,
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
    );

INSERT INTO orders (final_cost) VALUES (6.50);

INSERT INTO items (description, cost, quantity, order_id) VALUES
                                                              ('Apple', 0.60, 10, 1),
                                                              ('Orange', 0.25, 15, 1);
