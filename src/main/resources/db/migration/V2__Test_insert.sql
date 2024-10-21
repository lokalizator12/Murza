

CREATE TABLE IF NOT EXISTS shipping_methods (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                method_name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS items (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       item_name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO categories (category_name) VALUES
                                           ('Electronics'),
                                           ('Furniture'),
                                           ('Clothing'),
                                           ('Food'),
                                           ('Personal Items'),
                                           ('Sports Equipment');

INSERT IGNORE INTO shipping_methods (method_name) VALUES
                                               ('Car'),
                                               ('Bus'),
                                               ('Walking'),
                                               ('Motorcycle'),
                                               ('Bicycle'),
                                               ('Train'),
                                               ('Airplane'),
                                               ('Boat');

INSERT IGNORE INTO country (name) VALUES
                                       ('Germany'),
                                       ('France'),
                                       ('Russia'),
                                       ('Ukraine'),
                                       ('Belarus'),
                                       ('Poland'),
                                       ('Spain'),
                                       ('Italy'),
                                       ('Netherlands'),
                                       ('Sweden');

INSERT INTO cities (name, country_id) VALUES
                                             ('Berlin', (SELECT Id FROM country WHERE name = 'Germany')),
                                             ('Munich', (SELECT Id FROM country WHERE name = 'Germany')),
                                             ('Hamburg', (SELECT Id FROM country WHERE name = 'Germany')),
                                             ('Frankfurt', (SELECT Id FROM country WHERE name = 'Germany'));

-- Добавляем города для Франции
INSERT INTO cities (name, country_id) VALUES
                                             ('Paris', (SELECT Id FROM country WHERE name = 'France')),
                                             ('Marseille', (SELECT Id FROM country WHERE name = 'France')),
                                             ('Lyon', (SELECT Id FROM country WHERE name = 'France')),
                                             ('Nice', (SELECT Id FROM country WHERE name = 'France'));

-- Добавляем города для России
INSERT INTO cities (name, country_id) VALUES
                                             ('Moscow', (SELECT Id FROM country WHERE name = 'Russia')),
                                             ('Saint Petersburg', (SELECT Id FROM country WHERE name = 'Russia')),
                                             ('Novosibirsk', (SELECT Id FROM country WHERE name = 'Russia')),
                                             ('Yekaterinburg', (SELECT Id FROM country WHERE name = 'Russia'));

-- Добавляем города для Украины
INSERT INTO cities (name, country_id) VALUES
                                             ('Kyiv', (SELECT Id FROM country WHERE name = 'Ukraine')),
                                             ('Lviv', (SELECT Id FROM country WHERE name = 'Ukraine')),
                                             ('Odesa', (SELECT Id FROM country WHERE name = 'Ukraine')),
                                             ('Kharkiv', (SELECT Id FROM country WHERE name = 'Ukraine'));

-- Добавляем города для Беларуси
INSERT INTO cities (name, country_id) VALUES
                                             ('Minsk', (SELECT Id FROM country WHERE name = 'Belarus')),
                                             ('Brest', (SELECT Id FROM country WHERE name = 'Belarus')),
                                             ('Gomel', (SELECT Id FROM country WHERE name = 'Belarus')),
                                             ('Vitebsk', (SELECT Id FROM country WHERE name = 'Belarus'));

-- Добавляем города для Польши
INSERT INTO cities (name, country_id) VALUES
                                             ('Warsaw', (SELECT Id FROM country WHERE name = 'Poland')),
                                             ('Krakow', (SELECT Id FROM country WHERE name = 'Poland')),
                                             ('Wroclaw', (SELECT Id FROM country WHERE name = 'Poland')),
                                             ('Gdansk', (SELECT Id FROM country WHERE name = 'Poland'));

-- Добавляем города для Испании
INSERT INTO cities (name, country_id) VALUES
                                             ('Madrid', (SELECT Id FROM country WHERE name = 'Spain')),
                                             ('Barcelona', (SELECT Id FROM country WHERE name = 'Spain')),
                                             ('Valencia', (SELECT Id FROM country WHERE name = 'Spain')),
                                             ('Seville', (SELECT Id FROM country WHERE name = 'Spain'));

-- Добавляем города для Италии
INSERT INTO cities (name, country_id) VALUES
                                             ('Rome', (SELECT Id FROM country WHERE name = 'Italy')),
                                             ('Milan', (SELECT Id FROM country WHERE name = 'Italy')),
                                             ('Naples', (SELECT Id FROM country WHERE name = 'Italy')),
                                             ('Turin', (SELECT Id FROM country WHERE name = 'Italy'));

-- Добавляем города для Нидерландов
INSERT INTO cities (name, country_id) VALUES
                                             ('Amsterdam', (SELECT Id FROM country WHERE name = 'Netherlands')),
                                             ('Rotterdam', (SELECT Id FROM country WHERE name = 'Netherlands')),
                                             ('The Hague', (SELECT Id FROM country WHERE name = 'Netherlands')),
                                             ('Utrecht', (SELECT Id FROM country WHERE name = 'Netherlands'));

-- Добавляем города для Швеции
INSERT INTO cities (name, country_id) VALUES
                                             ('Stockholm', (SELECT Id FROM country WHERE name = 'Sweden')),
                                             ('Gothenburg', (SELECT Id FROM country WHERE name = 'Sweden')),
                                             ('Malmo', (SELECT Id FROM country WHERE name = 'Sweden')),
                                             ('Uppsala', (SELECT Id FROM country WHERE name = 'Sweden'));
