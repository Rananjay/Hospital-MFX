package com.hospital;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.function.Function;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PatientController {
    private final PatientDAO dao = new PatientDAO();
    private final TextField id = new TextField(), name = new TextField(),
        age = new TextField(), phone = new TextField(), diagnosis = new TextField(),
        search = new TextField();
    private final ComboBox<String> gender = new ComboBox<>();
    private final DatePicker date = new DatePicker();
    private final TableView<Patient> table = new TableView<>();
    private final Label status = new Label("Ready.");
    private final BorderPane root = new BorderPane();

    public PatientController() {
        id.setEditable(false);
        gender.getItems().addAll("Female", "Male", "Non-binary", "Prefer not to say");
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("Patient ID"), id);
        form.addRow(1, new Label("Full Name"), name);
        form.addRow(2, new Label("Age"), age);
        form.addRow(3, new Label("Gender"), gender);
        form.addRow(4, new Label("Phone"), phone);
        form.addRow(5, new Label("Diagnosis / Reason"), diagnosis);
        form.addRow(6, new Label("Admission Date"), date);

        HBox buttons = new HBox(8, button("Add", () -> save(false)),
            button("Update", () -> save(true)), button("Delete", this::delete),
            button("Clear", this::clear));
        search.setPromptText("Name or phone");
        HBox searchBar = new HBox(8, search, button("Search", () -> refresh(true)),
            button("Show All", this::loadPatients));
        search.setOnAction(event -> refresh(true));

        column("ID", Patient::getPatientId);
        column("Name", Patient::getName);
        column("Age", Patient::getAge);
        column("Gender", Patient::getGender);
        column("Phone", Patient::getPhone);
        column("Diagnosis", Patient::getDiagnosis);
        column("Admission Date", Patient::getAdmissionDate);
        table.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldPatient, patient) -> show(patient));

        VBox records = new VBox(10, searchBar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        HBox.setHgrow(records, Priority.ALWAYS);
        root.setPadding(new Insets(15));
        root.setTop(new Label("Hospital Management System — Patient Records"));
        root.setCenter(new HBox(20, new VBox(15, form, buttons), records));
        root.setBottom(status);
    }

    public BorderPane getRoot() { return root; }

    private Button button(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        return button;
    }

    private <T> void column(String title, Function<Patient, T> value) {
        TableColumn<Patient, T> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new SimpleObjectProperty<>(value.apply(cell.getValue())));
        table.getColumns().add(column);
    }

    public void loadPatients() { refresh(false); }

    private boolean refresh(boolean searching) {
        try {
            table.getItems().setAll(searching
                ? dao.searchPatients(search.getText().trim()) : dao.getAllPatients());
            status.setText(table.getItems().size() + " patient(s) found.");
            return true;
        } catch (SQLException error) {
            status.setText("Database error: " + error.getMessage());
            return false;
        }
    }

    private void show(Patient patient) {
        id.setText(patient == null ? "" : String.valueOf(patient.getPatientId()));
        name.setText(patient == null ? "" : patient.getName());
        age.setText(patient == null ? "" : String.valueOf(patient.getAge()));
        gender.setValue(patient == null ? null : patient.getGender());
        phone.setText(patient == null ? "" : patient.getPhone());
        diagnosis.setText(patient == null ? "" : patient.getDiagnosis());
        date.setValue(patient == null ? null : patient.getAdmissionDate());
    }

    private void clear() {
        table.getSelectionModel().clearSelection();
        show(null);
    }

    private void save(boolean update) {
        Patient selected = table.getSelectionModel().getSelectedItem();
        if (update && selected == null) {
            status.setText("Select a patient first.");
            return;
        }
        try {
            if (name.getText().trim().isEmpty() || age.getText().trim().isEmpty()
                || gender.getValue() == null || phone.getText().trim().isEmpty()
                || diagnosis.getText().trim().isEmpty() || date.getValue() == null)
                throw new IllegalArgumentException("Complete all fields.");
            int years;
            try {
                years = Integer.parseInt(age.getText().trim());
            } catch (NumberFormatException error) {
                throw new IllegalArgumentException("Age must be a whole number.");
            }
            if (years < 0 || years > 125)
                throw new IllegalArgumentException("Age must be between 0 and 125.");
            if (date.getValue().isAfter(LocalDate.now()))
                throw new IllegalArgumentException("Admission date cannot be in the future.");

            Patient patient = new Patient(update ? selected.getPatientId() : 0,
                name.getText().trim(), years, gender.getValue(), phone.getText().trim(),
                diagnosis.getText().trim(), date.getValue());
            boolean saved = update ? dao.updatePatient(patient) : dao.addPatient(patient);
            if (!saved) {
                status.setText("No record changed. Reload and try again.");
                return;
            }
            clear();
            if (refresh(false)) status.setText(update ? "Patient updated." : "Patient added.");
        } catch (IllegalArgumentException error) {
            status.setText(error.getMessage());
        } catch (SQLException error) {
            status.setText("Database error: " + error.getMessage());
        }
    }

    private void delete() {
        Patient patient = table.getSelectionModel().getSelectedItem();
        if (patient == null) {
            status.setText("Select a patient first.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete patient " + patient.getPatientId() + "?", ButtonType.OK, ButtonType.CANCEL);
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            if (!dao.deletePatient(patient.getPatientId())) {
                status.setText("Patient was not found. Reload the records.");
                return;
            }
            clear();
            if (refresh(false)) status.setText("Patient deleted.");
        } catch (SQLException error) {
            status.setText("Database error: " + error.getMessage());
        }
    }
}
