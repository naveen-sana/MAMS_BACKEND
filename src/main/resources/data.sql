-- Seed Demo Reference Data

INSERT IGNORE INTO bases (id, name, location) VALUES
(1, 'Base Alpha', 'New Delhi'),
(2, 'Base Bravo', 'Ladakh'),
(3, 'Base Charlie', 'Pathankot');

INSERT IGNORE INTO equipment_types (id, name, category) VALUES
(1, 'INSAS Rifle', 'WEAPON'),
(2, 'Armored Personnel Carrier', 'VEHICLE'),
(3, '5.56mm Ammo', 'AMMUNITION'),
(4, 'Tactical Helmet', 'GEAR');

INSERT IGNORE INTO personnel_units (id, name) VALUES
(1, 'Captain Arjun Sharma - Punjab Regiment'),
(2, 'Major Vikram Batra - 13 JAK Rifles'),
(3, 'Lieutenant Rajesh Kumar - Gorkha Rifles');
