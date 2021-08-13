DROP DATABASE IF EXISTS fungalf_the_grey;

CREATE DATABASE IF NOT EXISTS fungalf_the_grey DEFAULT CHARSET utf8;

USE fungalf_the_grey;

CREATE TABLE IF NOT EXISTS trainer(
    -- Snowflake of the discord user
    id LONG NOT NULL PRIMARY KEY,
    pokedollar INT NOT NULL
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pokemon(
    id INT PRIMARY KEY NOT NULL,
    trainerId LONG NOT NULL,
    xp INT NOT NULL,
    dateCaught DATETIME NOT NULL,
    FOREIGN KEY (trainerId) REFERENCES trainer(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS item(
    id INT PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS trainer_has_pokemon(
    trainerId LONG NOT NULL,
    pokemonId INT NOT NULL,
    PRIMARY KEY (trainerId, pokemonId),
    FOREIGN KEY (trainerId) REFERENCES trainer(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (pokemonId) REFERENCES pokemon(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS trainer_has_item(
    trainerId LONG NOT NULL,
    itemId INT NOT NULL,
    amount INT NOT NULL,
    PRIMARY KEY (trainerId, itemId),
    FOREIGN KEY (trainerId) REFERENCES trainer(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (itemId) REFERENCES item(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)ENGINE=InnoDB;
