USE restaurantdb;

-- ================================================
-- OPTION 1: DROP ALL TABLES (Complete Cleanup)
-- ================================================
-- This will remove all tables and their data permanently
-- Run this if you want to start completely fresh

-- Disable foreign key checks temporarily to allow dropping tables in any order
SET FOREIGN_KEY_CHECKS = 0;

-- Drop all tables (order doesn't matter when FK checks are disabled)
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS order_header;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS staff_tracker;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS loyalty_members;
DROP TABLE IF EXISTS tables;
DROP TABLE IF EXISTS staff;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- ================================================
-- OPTION 2: TRUNCATE ALL TABLES (Keep Structure, Remove Data)
-- ================================================
-- Uncomment the section below if you want to keep table structure but remove all data
-- Make sure to comment out the DROP TABLE section above if using this

/*
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE payments;
TRUNCATE TABLE order_item;
TRUNCATE TABLE order_header;
TRUNCATE TABLE reservations;
TRUNCATE TABLE staff_tracker;
TRUNCATE TABLE menu_items;
TRUNCATE TABLE loyalty_members;
TRUNCATE TABLE tables;
TRUNCATE TABLE staff;

SET FOREIGN_KEY_CHECKS = 1;
*/

-- ================================================
-- OPTION 3: DROP ENTIRE DATABASE
-- ================================================
-- Uncomment below to drop the entire database and recreate it
-- WARNING: This will delete EVERYTHING including the database itself

/*
DROP DATABASE IF EXISTS restaurantdb;
CREATE DATABASE restaurantdb
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
*/

-- ================================================
-- After running this script:
-- 1. If you used DROP TABLES: Run INFM11-12-2025.sql to recreate tables
-- 2. If you used TRUNCATE: Tables are empty but structure remains
-- 3. If you used DROP DATABASE: Run INFM11-12-2025.sql to recreate everything
-- ================================================

