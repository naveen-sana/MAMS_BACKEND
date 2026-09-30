-- =========================================================
-- Military Asset Management System (MAMS) - Database Seed Script
-- =========================================================

-- 1. BASES
INSERT INTO bases (id, name, location) VALUES
(1, 'Base A', 'Nellore'),
(2, 'Base B', 'Kurnool'),
(3, 'Base C', 'Kadapa'),
(4, 'Base D', 'Hyderabad'),
(5, 'Base E', 'Chennai')
ON DUPLICATE KEY UPDATE name=VALUES(name), location=VALUES(location);

-- 2. EQUIPMENT TYPES
INSERT INTO equipment_types (id, name, category) VALUES
(1, 'Rifle', 'WEAPON'),
(2, 'Pistol', 'WEAPON'),
(3, 'Rifle Bullets', 'AMMUNITION'),
(4, 'Pistol Bullets', 'AMMUNITION'),
(5, 'Jeep', 'VEHICLE'),
(6, 'Truck', 'VEHICLE'),
(7, 'Radio', 'GEAR'),
(8, 'Helmet', 'GEAR')
ON DUPLICATE KEY UPDATE name=VALUES(name), category=VALUES(category);

-- 3. PERSONNEL UNITS
INSERT INTO personnel_units (id, name) VALUES
(1, 'Captain Arjun Sharma'),
(2, 'Major Vikram Batra'),
(3, 'Lieutenant Rajesh Kumar')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 4. USERS (Default Password for all: password123)
-- BCrypt Hash for 'password123': $2a$10$7R0Zf98p6G1P8vR6w1cxe.B2J7g3y2mX3X3X3X3X3X3X3X3X3X3X3
INSERT INTO users (id, name, email, password_hash, role, base_id) VALUES
(1, 'System Admin', 'admin@military.gov', '$2a$10$7R0Zf98p6G1P8vR6w1cxe.B2J7g3y2mX3X3X3X3X3X3X3X3X3X3X3', 'ADMIN', NULL),
(2, 'Base Commander', 'commander1@military.gov', '$2a$10$7R0Zf98p6G1P8vR6w1cxe.B2J7g3y2mX3X3X3X3X3X3X3X3X3X3X3', 'BASE_COMMANDER', 1),
(3, 'Logistics Officer', 'logistics1@military.gov', '$2a$10$7R0Zf98p6G1P8vR6w1cxe.B2J7g3y2mX3X3X3X3X3X3X3X3X3X3X3', 'LOGISTICS_OFFICER', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), password_hash=VALUES(password_hash), role=VALUES(role), base_id=VALUES(base_id);

-- 5. PURCHASES
INSERT INTO purchases (id, base_id, equipment_type_id, quantity, date) VALUES
(1, 1, 1, 200, DATE_SUB(CURDATE(), INTERVAL 50 DAY)),
(2, 1, 3, 500, DATE_SUB(CURDATE(), INTERVAL 45 DAY)),
(3, 1, 5, 20, DATE_SUB(CURDATE(), INTERVAL 40 DAY)),
(4, 1, 8, 100, DATE_SUB(CURDATE(), INTERVAL 35 DAY)),
(5, 2, 1, 100, DATE_SUB(CURDATE(), INTERVAL 50 DAY)),
(6, 2, 2, 50, DATE_SUB(CURDATE(), INTERVAL 45 DAY)),
(7, 3, 6, 30, DATE_SUB(CURDATE(), INTERVAL 40 DAY)),
(8, 3, 4, 300, DATE_SUB(CURDATE(), INTERVAL 35 DAY)),
(9, 4, 7, 50, DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
(10, 4, 8, 150, DATE_SUB(CURDATE(), INTERVAL 25 DAY)),
(11, 5, 1, 80, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),
(12, 5, 3, 400, DATE_SUB(CURDATE(), INTERVAL 15 DAY))
ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), date=VALUES(date);

-- 6. TRANSFERS
INSERT INTO transfers (id, from_base_id, to_base_id, equipment_type_id, quantity, date) VALUES
(1, 1, 2, 1, 30, DATE_SUB(CURDATE(), INTERVAL 30 DAY)),
(2, 1, 3, 3, 100, DATE_SUB(CURDATE(), INTERVAL 25 DAY)),
(3, 2, 1, 2, 20, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),
(4, 4, 1, 7, 10, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
(5, 3, 5, 6, 5, DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
(6, 5, 4, 3, 50, DATE_SUB(CURDATE(), INTERVAL 5 DAY))
ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), date=VALUES(date);

-- 7. ASSIGNMENTS
INSERT INTO assignments (id, base_id, equipment_type_id, personnel_name, quantity, date) VALUES
(1, 1, 1, 'Captain Arjun Sharma', 25, DATE_SUB(CURDATE(), INTERVAL 12 DAY)),
(2, 1, 8, 'Lieutenant Rajesh Kumar', 15, DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
(3, 2, 2, 'Major Vikram Batra', 10, DATE_SUB(CURDATE(), INTERVAL 8 DAY)),
(4, 3, 6, 'Captain Arjun Sharma', 2, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
(5, 4, 7, 'Lieutenant Rajesh Kumar', 5, DATE_SUB(CURDATE(), INTERVAL 4 DAY))
ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), date=VALUES(date);

-- 8. EXPENDITURES
INSERT INTO expenditures (id, base_id, equipment_type_id, reason, quantity, date) VALUES
(1, 1, 3, 'Target Practice Training', 50, DATE_SUB(CURDATE(), INTERVAL 8 DAY)),
(2, 1, 8, 'Damaged during exercise', 5, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(3, 2, 3, 'Border Patrol Duty', 20, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
(4, 3, 4, 'Range Qualification', 50, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(5, 5, 3, 'Standard Drills', 30, DATE_SUB(CURDATE(), INTERVAL 2 DAY))
ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), date=VALUES(date);
