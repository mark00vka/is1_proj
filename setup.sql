-- ============================================================
-- SETUP - Kreiranje MySQL korisnika za projekat
-- Pokrenuti OVU SKRIPTU PRVO, pre podsistem1/2/3.sql
-- ============================================================

-- Kreiranje korisnika (ako vec ne postoji)
CREATE USER IF NOT EXISTS 'root'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
