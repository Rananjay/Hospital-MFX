package com.hospital;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    // Local development settings: replace these with your MySQL details.
    private static final String URL = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private Connection connect() throws SQLException {
        if (USER == null || PASSWORD == null) {
            throw new SQLException(
                "Set DB_USER and DB_PASSWORD in your run configuration."
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    public List<Patient> getAllPatients() throws SQLException {
        return searchPatients("");
    }

    public List<Patient> searchPatients(String keyword) throws SQLException {
        String sql = "SELECT * FROM patients WHERE name LIKE ? OR phone LIKE ? ORDER BY patient_id";
        List<Patient> patients = new ArrayList<>();
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + keyword + "%");
            statement.setString(2, "%" + keyword + "%");
            try (ResultSet row = statement.executeQuery()) {
                while (row.next()) {
                    patients.add(new Patient(row.getInt("patient_id"), row.getString("name"),
                        row.getInt("age"), row.getString("gender"), row.getString("phone"),
                        row.getString("diagnosis"), row.getDate("admission_date").toLocalDate()));
                }
            }
        }
        return patients;
    }

    public boolean addPatient(Patient patient) throws SQLException {
        return save(patient, false);
    }

    public boolean updatePatient(Patient patient) throws SQLException {
        return save(patient, true);
    }

    private boolean save(Patient patient, boolean update) throws SQLException {
        String sql = update
            ? "UPDATE patients SET name=?, age=?, gender=?, phone=?, diagnosis=?, admission_date=? WHERE patient_id=?"
            : "INSERT INTO patients (name, age, gender, phone, diagnosis, admission_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, patient.getName());
            statement.setInt(2, patient.getAge());
            statement.setString(3, patient.getGender());
            statement.setString(4, patient.getPhone());
            statement.setString(5, patient.getDiagnosis());
            statement.setDate(6, Date.valueOf(patient.getAdmissionDate()));
            if (update) statement.setInt(7, patient.getPatientId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deletePatient(int id) throws SQLException {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                 "DELETE FROM patients WHERE patient_id=?")) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }
}
