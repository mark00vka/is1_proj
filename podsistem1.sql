-- ============================================================
-- PODSISTEM 1 - Gradovi, Korisnici, Uloge
-- ============================================================

CREATE DATABASE IF NOT EXISTS podsistem1
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE podsistem1;

-- ------------------------------------------------------------
-- Grad
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS grad (
    id_grad INT          NOT NULL AUTO_INCREMENT,
    naziv   VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_grad)
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Uloga
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS uloga (
    id_uloga INT          NOT NULL AUTO_INCREMENT,
    naziv    VARCHAR(100) NOT NULL,
    opis     TEXT,
    PRIMARY KEY (id_uloga),
    UNIQUE KEY uq_uloga_naziv (naziv)
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Korisnik
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS korisnik (
    id_korisnik     INT          NOT NULL AUTO_INCREMENT,
    korisnicko_ime  VARCHAR(100) NOT NULL,
    sifra           VARCHAR(255) NOT NULL,
    ime             VARCHAR(100) NOT NULL,
    prezime         VARCHAR(100) NOT NULL,
    adresa          VARCHAR(255),
    stanje_novca    INT          NOT NULL DEFAULT 0,
    id_grad         INT,
    PRIMARY KEY (id_korisnik),
    UNIQUE KEY uq_korisnicko_ime (korisnicko_ime),
    CONSTRAINT fk_korisnik_grad
        FOREIGN KEY (id_grad) REFERENCES grad (id_grad)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Korisnik <-> Uloga  (many-to-many)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS korisnik_uloga (
    id_korisnik INT NOT NULL,
    id_uloga    INT NOT NULL,
    PRIMARY KEY (id_korisnik, id_uloga),
    CONSTRAINT fk_ku_korisnik
        FOREIGN KEY (id_korisnik) REFERENCES korisnik (id_korisnik)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_ku_uloga
        FOREIGN KEY (id_uloga) REFERENCES uloga (id_uloga)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

-- ============================================================
-- Inicijalni podaci
-- ============================================================

INSERT INTO grad (naziv) VALUES
    ('Beograd'),
    ('Novi Sad'),
    ('Niš'),
    ('Kragujevac'),
    ('Subotica');

INSERT INTO uloga (naziv, opis) VALUES
    ('administrator', 'Pun pristup svim funkcionalnostima sistema'),
    ('prodavac',      'Može kreirati i upravljati artiklima'),
    ('kupac',         'Može pregledati artikle i vršiti kupovinu');

-- Lozinke su hash vrednosti; ovde koristimo placeholder vrednosti
INSERT INTO korisnik (korisnicko_ime, sifra, ime, prezime, adresa, stanje_novca, id_grad) VALUES
    ('admin',     'hashed_admin123',  'Petar',   'Petrović', 'Knez Mihailova 1',  100000, 1),
    ('prodavac1', 'hashed_prod123',   'Marko',   'Marković', 'Terazije 5',         50000, 1),
    ('kupac1',    'hashed_kupac123',  'Ana',     'Anić',     'Bulevar Oslobođenja', 30000, 2),
    ('kupac2',    'hashed_kupac456',  'Jovana',  'Jovanović','Cara Dušana 10',      20000, 3),
    ('prodavac2', 'hashed_prod456',   'Stefan',  'Stefanović','Kralja Petra 7',     40000, 1);

-- Dodeljivanje uloga
INSERT INTO korisnik_uloga (id_korisnik, id_uloga) VALUES
    (1, 1), -- admin -> administrator
    (2, 2), -- prodavac1 -> prodavac
    (3, 3), -- kupac1 -> kupac
    (4, 3), -- kupac2 -> kupac
    (5, 2); -- prodavac2 -> prodavac
