-- ============================================================
-- PODSISTEM 2 - Kategorije, Artikli, Korpa, Wishlist, Korisnici
-- ============================================================

CREATE DATABASE IF NOT EXISTS podsistem2
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE podsistem2;

-- ------------------------------------------------------------
-- Korisnik  (lokalna kopija – samo podaci potrebni ovom podsistemu)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS korisnik (
    id_korisnik    INT          NOT NULL,
    korisnicko_ime VARCHAR(100) NOT NULL,
    sifra          VARCHAR(255) NOT NULL,
    ime            VARCHAR(100) NOT NULL,
    prezime        VARCHAR(100) NOT NULL,
    adresa         VARCHAR(255),
    stanje_novca   INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id_korisnik),
    UNIQUE KEY uq_korisnicko_ime (korisnicko_ime)
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Kategorija  (self-referencing hierarhija)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS kategorija (
    id_kategorija  INT          NOT NULL AUTO_INCREMENT,
    naziv          VARCHAR(100) NOT NULL,
    id_nadkategorija INT        DEFAULT NULL,
    PRIMARY KEY (id_kategorija),
    CONSTRAINT fk_kategorija_nadkat
        FOREIGN KEY (id_nadkategorija) REFERENCES kategorija (id_kategorija)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Artikl
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS artikl (
    id_artikl   INT            NOT NULL AUTO_INCREMENT,
    naziv       VARCHAR(200)   NOT NULL,
    opis        TEXT,
    cena        DOUBLE         NOT NULL,
    popust      DOUBLE         NOT NULL DEFAULT 0.0,
    id_kategorija INT          NOT NULL,
    id_kreator  INT            NOT NULL,
    PRIMARY KEY (id_artikl),
    CONSTRAINT fk_artikl_kategorija
        FOREIGN KEY (id_kategorija) REFERENCES kategorija (id_kategorija)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_artikl_kreator
        FOREIGN KEY (id_kreator) REFERENCES korisnik (id_korisnik)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Korpa  (0..1 po korisniku)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS korpa (
    id_korpa     INT    NOT NULL AUTO_INCREMENT,
    ukupna_cena  DOUBLE NOT NULL DEFAULT 0.0,
    id_korisnik  INT    NOT NULL,
    PRIMARY KEY (id_korpa),
    UNIQUE KEY uq_korpa_korisnik (id_korisnik),
    CONSTRAINT fk_korpa_korisnik
        FOREIGN KEY (id_korisnik) REFERENCES korisnik (id_korisnik)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- KorpaArtikl  (artikli u korpi sa količinom)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS korpa_artikl (
    id_korpa  INT NOT NULL,
    id_artikl INT NOT NULL,
    kolicina  INT NOT NULL DEFAULT 1,
    PRIMARY KEY (id_korpa, id_artikl),
    CONSTRAINT fk_ka_korpa
        FOREIGN KEY (id_korpa) REFERENCES korpa (id_korpa)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_ka_artikl
        FOREIGN KEY (id_artikl) REFERENCES artikl (id_artikl)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Wishlist  (0..1 po korisniku)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS wishlist (
    id_wishlist     INT      NOT NULL AUTO_INCREMENT,
    datum_kreiranja DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_korisnik     INT      NOT NULL,
    PRIMARY KEY (id_wishlist),
    UNIQUE KEY uq_wishlist_korisnik (id_korisnik),
    CONSTRAINT fk_wishlist_korisnik
        FOREIGN KEY (id_korisnik) REFERENCES korisnik (id_korisnik)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- WishlistArtikl
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS wishlist_artikl (
    id_wishlist      INT      NOT NULL,
    id_artikl        INT      NOT NULL,
    datum_dodavanja  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_wishlist, id_artikl),
    CONSTRAINT fk_wa_wishlist
        FOREIGN KEY (id_wishlist) REFERENCES wishlist (id_wishlist)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_wa_artikl
        FOREIGN KEY (id_artikl) REFERENCES artikl (id_artikl)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

-- ============================================================
-- Inicijalni podaci
-- ============================================================

INSERT INTO korisnik (id_korisnik, korisnicko_ime, sifra, ime, prezime, adresa, stanje_novca) VALUES
    (1, 'admin',     'hashed_admin123', 'Petar',  'Petrović',  'Knez Mihailova 1',   100000),
    (2, 'prodavac1', 'hashed_prod123',  'Marko',  'Marković',  'Terazije 5',          50000),
    (3, 'kupac1',    'hashed_kupac123', 'Ana',    'Anić',      'Bulevar Oslobođenja', 30000),
    (4, 'kupac2',    'hashed_kupac456', 'Jovana', 'Jovanović', 'Cara Dušana 10',      20000),
    (5, 'prodavac2', 'hashed_prod456',  'Stefan', 'Stefanović','Kralja Petra 7',      40000);

INSERT INTO kategorija (naziv, id_nadkategorija) VALUES
    ('Elektronika',   NULL),   -- 1
    ('Odeća',         NULL),   -- 2
    ('Hrana',         NULL),   -- 3
    ('Telefoni',      1),      -- 4 (pod Elektronika)
    ('Laptopovi',     1),      -- 5 (pod Elektronika)
    ('Muška odeća',   2),      -- 6
    ('Ženska odeća',  2);      -- 7

INSERT INTO artikl (naziv, opis, cena, popust, id_kategorija, id_kreator) VALUES
    ('iPhone 15',      'Najnoviji Apple telefon',        120000.0, 5.0,  4, 2),
    ('Samsung Galaxy', 'Android flagship telefon',        90000.0, 10.0, 4, 2),
    ('MacBook Pro',    'Profesionalni laptop',            200000.0, 0.0,  5, 5),
    ('Muška jakna',    'Zimska muška jakna, vel. L',       8000.0, 15.0, 6, 5),
    ('Ženska haljina', 'Elegantna haljina za priredbe',   5000.0, 20.0, 7, 2);

-- Korpe
INSERT INTO korpa (ukupna_cena, id_korisnik) VALUES
    (0.0, 3),
    (0.0, 4);

-- Sadržaj korpi
INSERT INTO korpa_artikl (id_korpa, id_artikl, kolicina) VALUES
    (1, 1, 1),  -- kupac1: iPhone 15
    (1, 4, 2),  -- kupac1: 2x Muška jakna
    (2, 3, 1);  -- kupac2: MacBook Pro

-- Recalculate ukupna_cena
UPDATE korpa k
SET ukupna_cena = (
    SELECT COALESCE(SUM(a.cena * (1 - a.popust/100) * ka.kolicina), 0)
    FROM korpa_artikl ka
    JOIN artikl a ON a.id_artikl = ka.id_artikl
    WHERE ka.id_korpa = k.id_korpa
);

-- Wishlist
INSERT INTO wishlist (datum_kreiranja, id_korisnik) VALUES
    (NOW(), 3),
    (NOW(), 4);

INSERT INTO wishlist_artikl (id_wishlist, id_artikl, datum_dodavanja) VALUES
    (1, 3, NOW()),  -- kupac1 želi MacBook
    (1, 5, NOW()),  -- kupac1 želi Ženska haljina
    (2, 1, NOW());  -- kupac2 želi iPhone
