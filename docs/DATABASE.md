# Database Schema

```sql
-- docs/schema.sql
-- TechMart E-Commerce Optimized Database Schema

-- 1. Users Table
CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50) UNIQUE  NOT NULL,
    email         VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_users_email ON users (email);

-- 2. Products Table (with Optimistic Locking for concurrency)
CREATE TABLE products
(
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(100)   NOT NULL,
    image_url      VARCHAR(500)   NOT NULL,
    description    TEXT,
    price          DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    stock_quantity INT            NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    version        INT            NOT NULL DEFAULT 0, -- Critical for preventing overselling
    created_at     TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP               DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_products_name ON products (name);
CREATE INDEX idx_products_stock ON products (stock_quantity);
-- Speeds up "low stock" queries

-- 3. Orders Table
CREATE TABLE orders
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    total_amount DECIMAL(10, 2) NOT NULL,
    status       VARCHAR(20)    NOT NULL DEFAULT 'PENDING', -- PENDING, PROCESSING, COMPLETED, FAILED
    version      INT            NOT NULL DEFAULT 0,
    created_at   TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP               DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
-- Critical for querying pending orders

-- 4. Order Items Table
CREATE TABLE order_items
(
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT         NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    product_id BIGINT         NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    quantity   INT            NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL -- Denormalized: preserves historical price
);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);


-- =================================================================
-- Initial Seed Data (For Testing & Demonstration)
-- =================================================================
INSERT INTO users (username, email, password_hash)
VALUES ('testuser', 'test@techmart.com', 'hashed_password_123');

INSERT INTO products (name, description, image_url, price, stock_quantity)
VALUES ('Laptop Pro', 'High-performance laptop', 'https://picsum.photos/seed/mouse/400/400', 1200.00, 100),
       ('Wireless Mouse', 'Ergonomic wireless mouse', 'https://picsum.photos/seed/mouse/400/400', 25.50, 500),
       ('Mechanical Keyboard', 'RGB mechanical keyboard', 'https://picsum.photos/seed/mouse/400/400', 85.00, 150);

```