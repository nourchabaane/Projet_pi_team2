CREATE DATABASE IF NOT EXISTS 3A61;
USE 3A61;

CREATE TABLE IF NOT EXISTS medicament (
                                          id INT PRIMARY KEY AUTO_INCREMENT,
                                          nom VARCHAR(255) NOT NULL,
                                          description TEXT,
                                          email VARCHAR(255),
                                          phone VARCHAR(20),
                                          dosage VARCHAR(50),
                                          schedule VARCHAR(100)
);