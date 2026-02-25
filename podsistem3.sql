-- ============================================================
-- PODSISTEM 3 - Narudžbine, Transakcije, Stavke, Artikli, Korisnici
-- ============================================================

CREATE DATABASE IF NOT EXISTS podsistem3
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE podsistem3;

-- ------------------------------------------------------------
-- Korisnik  (lokalna kopija)
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
-- Grad  (lokalna kopija – samo za adresu dostave)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS grad (
    id_grad INT          NOT NULL,
    naziv   VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_grad)
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Artikl  (lokalna kopija – snapshot u trenutku kupovine)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS artikl (
    id_artikl INT          NOT NULL,
    naziv     VARCHAR(200) NOT NULL,
    opis      TEXT,
    cena      DOUBLE       NOT NULL,
    popust    DOUBLE       NOT NULL DEFAULT 0.0,
    PRIMARY KEY (id_artikl)
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Narudzbina
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS narudzbina (
    id_narudzbine  INT          NOT NULL AUTO_INCREMENT,
    ukupna_cena    DOUBLE       NOT NULL,
    vreme_kreiranja DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    adresa         VARCHAR(255) NOT NULL,
    id_grad_dostava INT         NOT NULL,
    id_kupac       INT          NOT NULL,
    PRIMARY KEY (id_narudzbine),
    CONSTRAINT fk_narudzbina_grad
        FOREIGN KEY (id_grad_dostava) REFERENCES grad (id_grad)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_narudzbina_kupac
        FOREIGN KEY (id_kupac) REFERENCES korisnik (id_korisnik)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Stavka  (linija narudžbine)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS stavka (
    id_stavka      INT    NOT NULL AUTO_INCREMENT,
    kolicina       INT    NOT NULL,
    jedinicna_cena DOUBLE NOT NULL,
    id_narudzbine  INT    NOT NULL,
    id_artikl      INT    NOT NULL,
    PRIMARY KEY (id_stavka),
    CONSTRAINT fk_stavka_narudzbina
        FOREIGN KEY (id_narudzbine) REFERENCES narudzbina (id_narudzbine)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_stavka_artikl
        FOREIGN KEY (id_artikl) REFERENCES artikl (id_artikl)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

-- ------------------------------------------------------------
-- Transakcija
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS transakcija (
    id_transakcija INT      NOT NULL AUTO_INCREMENT,
    placena_suma   DOUBLE   NOT NULL,
    vreme_placanja DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_narudzbine  INT      NOT NULL,
    id_kupac       INT      NOT NULL,
    PRIMARY KEY (id_transakcija),
    UNIQUE KEY uq_transakcija_narudzbina (id_narudzbine),   -- 1:1 veza
    CONSTRAINT fk_transakcija_narudzbina
        FOREIGN KEY (id_narudzbine) REFERENCES narudzbina (id_narudzbine)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_transakcija_kupac
        FOREIGN KEY (id_kupac) REFERENCES korisnik (id_korisnik)
        ON UPDATE CASCADE ON DELETE RESTRICT
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

INSERT INTO grad (id_grad, naziv) VALUES
    (1, 'Beograd'),
    (2, 'Novi Sad'),
    (3, 'Niš'),
    (4, 'Kragujevac'),
    (5, 'Subotica');

INSERT INTO artikl (id_artikl, naziv, opis, cena, popust) VALUES
    (1, 'iPhone 15',      'Najnoviji Apple telefon',        120000.0, 5.0),
    (2, 'Samsung Galaxy', 'Android flagship telefon',        90000.0, 10.0),
    (3, 'MacBook Pro',    'Profesionalni laptop',            200000.0, 0.0),
    (4, 'Muška jakna',    'Zimska muška jakna, vel. L',       8000.0, 15.0),
    (5, 'Ženska haljina', 'Elegantna haljina za priredbe',   5000.0, 20.0);

-- Narudžbina 1: kupac1 (id=3) poručio iPhone i Mušku jaknu
INSERT INTO narudzbina (ukupna_cena, vreme_kreiranja, adresa, id_grad_dostava, id_kupac) VALUES
    (122000.0, '2025-11-10 14:30:00', 'Bulevar Oslobođenja 22', 2, 3),
    (200000.0, '2025-12-01 10:00:00', 'Cara Dušana 10',         3, 4);

INSERT INTO stavka (kolicina, jedinicna_cena, id_narudzbine, id_artikl) VALUES
    (1, 114000.0, 1, 1),  -- iPhone 15 sa popustom 5%
    (1,   6800.0, 1, 4),  -- Muška jakna sa popustom 15%
    (1, 200000.0, 2, 3);  -- MacBook Pro bez popusta

INSERT INTO transakcija (placena_suma, vreme_placanja, id_narudzbine, id_kupac) VALUES
    (120800.0, '2025-11-10 14:31:00', 1, 3),
    (200000.0, '2025-12-01 10:01:00', 2, 4);
