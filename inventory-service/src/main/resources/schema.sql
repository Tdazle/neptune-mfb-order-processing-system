CREATE TABLE IF NOT EXISTS products (
                                       id SERIAL PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
                                       stock_quantity INTEGER NOT NULL
);
INSERT INTO products (name, stock_quantity) VALUES ('Laptop', 10);
INSERT INTO products (name, stock_quantity) VALUES ('Phone', 20);
