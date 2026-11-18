CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

CREATE TABLE IF NOT EXISTS patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(30) NOT NULL,
    phone VARCHAR(40) NOT NULL,
    diagnosis VARCHAR(500) NOT NULL,
    admission_date DATE NOT NULL
);
