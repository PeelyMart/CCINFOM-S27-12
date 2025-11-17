USE restaurantdb;

-- ================================================
-- COMPLETE DATABASE RESET
-- ================================================
-- This script will:
-- 1. Drop all tables
-- 2. Recreate all tables with structure
-- 3. Insert initial data (staff, tables, menu items, etc.)
-- ================================================

-- Step 1: Drop all existing tables
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS order_header;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS staff_tracker;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS loyalty_members;
DROP TABLE IF EXISTS tables;
DROP TABLE IF EXISTS staff;

SET FOREIGN_KEY_CHECKS = 1;

-- Step 2: Recreate all tables
-- ----------------------
-- Table: staff
-- ----------------------
CREATE TABLE IF NOT EXISTS staff (
    staff_id INT NOT NULL AUTO_INCREMENT,
    staff_pin INT NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    contact_number VARCHAR(11) NOT NULL,
    PRIMARY KEY (staff_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------
-- Table: staff_tracker
-- ----------------------
CREATE TABLE IF NOT EXISTS staff_tracker (
    session_id INT NOT NULL AUTO_INCREMENT,
    staff_id INT NOT NULL,
    date DATE NOT NULL,
    time_in DATETIME DEFAULT NULL,
    time_out DATETIME DEFAULT NULL,
    session_minutes INT DEFAULT NULL,
    PRIMARY KEY (session_id),
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------
-- Table: tables
-- ----------------------
CREATE TABLE IF NOT EXISTS tables (
    table_id INT NOT NULL AUTO_INCREMENT,
    capacity INT NOT NULL,
    is_available TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (table_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------
-- Table: loyalty_members
-- ----------------------
CREATE TABLE IF NOT EXISTS loyalty_members (
    customer_id INT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    contact_number VARCHAR(10),
    join_date DATE DEFAULT NULL,
    points INT DEFAULT 0,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------
-- Table: menu_items
-- ----------------------
CREATE TABLE IF NOT EXISTS menu_items (
    menu_id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT DEFAULT NULL,
    price DECIMAL(10,2) NOT NULL,
    is_available TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------
-- Table: order_header
-- ----------------------
CREATE TABLE IF NOT EXISTS order_header (
    order_id INT NOT NULL AUTO_INCREMENT,
    table_id INT NOT NULL,
    staff_id INT NOT NULL,
    order_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_cost DECIMAL(10,2) DEFAULT 0.00,
    status ENUM('closed','open','cancelled') DEFAULT 'open',
    PRIMARY KEY (order_id),
    FOREIGN KEY (table_id) REFERENCES tables(table_id),
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------
-- Table: order_item
-- ----------------------
CREATE TABLE IF NOT EXISTS order_item (
    order_item_id INT NOT NULL AUTO_INCREMENT,
    order_id INT NOT NULL,
    menu_id INT NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (order_item_id),
    FOREIGN KEY (order_id) REFERENCES order_header(order_id),
    FOREIGN KEY (menu_id) REFERENCES menu_items(menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------
-- Table: payments
-- ----------------------
CREATE TABLE IF NOT EXISTS payments (
    transaction_id INT NOT NULL AUTO_INCREMENT,
    order_id INT NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL,
    staff_id INT NOT NULL,
    loyal_customer_id INT DEFAULT NULL,
    unknown_customer_name VARCHAR(50) DEFAULT NULL,
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    payment_method ENUM('CASH','DEBIT','CREDIT') NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (transaction_id),
    FOREIGN KEY (order_id) REFERENCES order_header(order_id),
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id),
    FOREIGN KEY (loyal_customer_id) REFERENCES loyalty_members(customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------
-- Table: reservations
-- ----------------------
CREATE TABLE IF NOT EXISTS reservations (
    request_id INT NOT NULL AUTO_INCREMENT,
    table_id INT NOT NULL,
    reserve_name VARCHAR(50) NOT NULL,
    date_and_time DATETIME NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (request_id),
    FOREIGN KEY (table_id) REFERENCES tables(table_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Step 3: Insert initial data
-- Staff
INSERT INTO staff (staff_pin, first_name, last_name, contact_number)
VALUES
    (1234, 'John', 'Doe', '09171234567'),
    (5678, 'Jane', 'Smith', '09179876543'),
    (9012, 'Michael', 'Brown', '09171239876'),
    (3456, 'Emily', 'Davis', '09172345678');

-- Tables (11 tables)
INSERT INTO tables (capacity, is_available)
VALUES
    (4, 1), (2, 1), (6, 1), (4, 1), (8, 1),
    (4, 1), (2, 1), (6, 1), (4, 1), (8, 1),
    (4, 1);

-- Loyalty Members
INSERT INTO loyalty_members (first_name, last_name, contact_number, join_date, points, is_active)
VALUES
    ('Alice', 'Reyes', '0917001122', '2024-01-15', 120, 1),
    ('Mark', 'Santos', '0917555333', '2024-02-05', 90, 1),
    ('Lea', 'Torres', '0917888999', '2024-03-10', 50, 1);

-- Menu Items
INSERT INTO menu_items (name, description, price, is_available)
VALUES
    ('Burger', 'Juicy beef burger', 100.00, 1),
    ('Fried Chicken', '2-piece crispy chicken', 150.00, 1),
    ('Pasta', 'Creamy carbonara pasta', 250.00, 1),
    ('Milkshake', 'Vanilla milkshake', 80.00, 1),
    ('Steak', 'Grilled sirloin steak', 350.00, 1),
    ('Pizza', 'Margherita pizza', 200.00, 1);

-- ================================================
-- Database reset complete!
-- All tables recreated and populated with initial data
-- ================================================

