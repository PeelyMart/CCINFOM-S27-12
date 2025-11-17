USE restaurantdb;

-- ================================================
-- TEST ORDERS (OPEN STATUS) - These will show as GREY buttons
-- ================================================
-- Note: Run these statements one at a time or ensure your SQL client supports LAST_INSERT_ID()

-- Order for Table 1 (OPEN - will show as grey)
INSERT INTO order_header (table_id, staff_id, total_cost, status, order_time)
VALUES (1, 1, 150.00, 'open', NOW());

SET @order_id_1 = LAST_INSERT_ID();
INSERT INTO order_item (order_id, menu_id, quantity, subtotal, is_active) VALUES
(@order_id_1, 1, 1, 100.00, 1),  -- Burger
(@order_id_1, 4, 1, 80.00, 1);   -- Milkshake

-- Order for Table 3 (OPEN - will show as grey)
INSERT INTO order_header (table_id, staff_id, total_cost, status, order_time)
VALUES (3, 1, 400.00, 'open', NOW());

SET @order_id_3 = LAST_INSERT_ID();
INSERT INTO order_item (order_id, menu_id, quantity, subtotal, is_active) VALUES
(@order_id_3, 3, 1, 250.00, 1),  -- Pasta
(@order_id_3, 5, 1, 350.00, 0);  -- Steak (completed)

-- Order for Table 5 (OPEN - will show as grey)
INSERT INTO order_header (table_id, staff_id, total_cost, status, order_time)
VALUES (5, 2, 300.00, 'open', NOW());

SET @order_id_5 = LAST_INSERT_ID();
INSERT INTO order_item (order_id, menu_id, quantity, subtotal, is_active) VALUES
(@order_id_5, 2, 2, 300.00, 1);  -- Fried Chicken x2

-- Order for Table 6 (OPEN - will show as grey)
INSERT INTO order_header (table_id, staff_id, total_cost, status, order_time)
VALUES (6, 2, 500.00, 'open', NOW());

SET @order_id_6 = LAST_INSERT_ID();
INSERT INTO order_item (order_id, menu_id, quantity, subtotal, is_active) VALUES
(@order_id_6, 5, 1, 350.00, 1),  -- Steak
(@order_id_6, 4, 2, 160.00, 0);  -- Milkshake x2 (completed)

-- Order for Table 8 (OPEN - will show as grey)
INSERT INTO order_header (table_id, staff_id, total_cost, status, order_time)
VALUES (8, 3, 250.00, 'open', NOW());

SET @order_id_8 = LAST_INSERT_ID();
INSERT INTO order_item (order_id, menu_id, quantity, subtotal, is_active) VALUES
(@order_id_8, 3, 1, 250.00, 1);  -- Pasta

-- ================================================
-- NOTE: Tables 2, 4, 7, 9, 10, 11 will show as GREEN (no open orders)
-- ================================================

