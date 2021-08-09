DROP DATABASE IF EXISTS fungalf_the_grey;

CREATE DATABASE IF NOT EXISTS fungalf_the_grey DEFAULT CHARSET utf8;

CREATE TABLE IF NOT EXISTS trainer(
    id INT PRIMARY KEY NOT NULL,
    snowflake LONG NOT NULL,
    pokedollar INT NOT NULL
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pokemon(
    id INT PRIMARY KEY NOT NULL,
    trainerId INT NOT NULL,
    xp INT NOT NULL,
    dateCaught DATETIME NOT NULL,
    FOREIGN KEY (trainerId) REFERENCES trainer(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS trainer_has_pokemon(
    trainerId INT NOT NULL,
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
    trainerId INT NOT NULL,
    itemName VARCHAR(255) NOT NULL,
    itemAmount INT NOT NULL,
    PRIMARY KEY (trainerId, itemName),
    FOREIGN KEY (trainerId) REFERENCES trainer(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)ENGINE=InnoDB;
