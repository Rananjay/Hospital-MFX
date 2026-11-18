# Hospital Management System — short, explainable version

## 1. Project structure

```text
src/
  application/Main.java
  com/hospital/Patient.java
  com/hospital/PatientController.java
  com/hospital/PatientDAO.java
database.sql
```

Replace your old App.java entry point with application/Main.java. Keep each public
class in its own matching file. The other three classes remain in com.hospital.

## 2. Before running

This is replacement source code for your existing JavaFX project, not a bundled
JavaFX SDK or a fully configured Maven project. No pom.xml or module-info.java was
provided, so your existing dependency setup is not overwritten.

1. Keep JavaFX Controls and MySQL Connector/J available in your project's dependencies.
2. Run database.sql in MySQL. Back up existing data first; this script does not migrate
   or repair an existing patients table with a different schema.
3. In PatientDAO.java, set URL, USER, and PASSWORD to your local MySQL settings.
4. Copy the source files into the matching packages in Eclipse.
5. Remove the old App.java or stop using its launch configuration.
6. Set the launch main class to application.Main instead of com.hospital.App.
7. Keep the JavaFX VM arguments from your existing working setup. If your build
   specifies a main class separately, change that setting to application.Main too.

If your project has module-info.java, retain the JavaFX Controls and java.sql
requirements and ensure application is exported to javafx.graphics. For example,
the relevant directives inside your existing module are:

```java
requires javafx.controls;
requires java.sql;
exports application to javafx.graphics;
```

Retain any other directives your project/dependency setup needs. The table uses
lambda cell factories rather than reflective PropertyValueFactory lookups.

## 3. How to use it

- Add: fill every field except the automatically generated patient ID, then click Add.
- Update: select a table row, edit the form, then click Update.
- Delete: select a row, click Delete, and confirm.
- Clear: empty the form and deselect the row.
- Search: enter part of a name or phone number, then click Search or press Enter.
- Show All: reload all records. An empty search also returns all records.
- Check the message at the bottom for validation or database errors.

Age must be a whole number from 0 to 125. Admission dates cannot be in the future.

## 4. Explain the program in one minute

"My project has four classes. Main starts the JavaFX window. Patient stores one
patient's details. PatientController builds the interface, validates inputs, and
responds to buttons. PatientDAO runs SQL to store and retrieve records in MySQL."

Data flow:

```text
User -> PatientController -> PatientDAO -> MySQL
MySQL -> PatientDAO -> Patient objects -> TableView
```

## 5. What each class does

### Main.java — entry point
- Extends Application.
- start() builds a controller, displays its root in a Scene, and loads patients.
- main() calls launch() to start JavaFX.

### Patient.java — model
- Represents one database row.
- Constructor fills the fields; getters read them.
- Fields are final: editing creates a replacement Patient object rather than
  changing the selected object. The original setters were unused and removed.

### PatientController.java — interface and actions
- Constructor creates the form, buttons, table, and selection listener once.
- button() avoids repeating button event-handler code.
- column() avoids repeating the same table-column setup seven times.
- show() copies the selected patient into the form; null empties the form.
- save(false) adds; save(true) updates. Both share the same validation.
- refresh() loads search results or all patients.
- delete() asks for confirmation before calling the DAO.

### PatientDAO.java — database access
- connect() opens the database connection.
- searchPatients() retrieves matching rows and builds Patient objects.
- getAllPatients() reuses the search with an empty keyword.
- save() shares parameter binding between INSERT and UPDATE.
- deletePatient() deletes by patient ID.
- PreparedStatement placeholders (?) keep input separate from SQL syntax.
- try-with-resources closes connections, statements, and result sets.

## 6. Small syntax guide

- `() -> save(false)`: an action to run later when a button is clicked.
- `Patient::getName`: a reference to the getter used for a table column.
- `<T>` in column(): lets the same helper support text, numbers, and dates.
- `condition ? a : b`: choose a when true, otherwise b.
- `throws SQLException`: let the caller handle a database failure.

## 7. What changed from your original

Kept: separate model/controller/DAO, same seven patient fields, MySQL storage,
add/update/delete/search/show-all, selection-to-form behavior, age/date checks,
and confirmation before deletion.

Shortened: shared add/update handling, shared query logic, reusable column/button
helpers, automatic SQL resource cleanup, and removed unused setters.

Small behavior changes: empty searches show all patients; Clear only resets the
form without replacing the current status; successful changes reload all records.
The DAO still reports affected rows, so the UI does not blindly report success.

## 8. Limits and safety

This is a classroom/local demonstration, not a production medical-record system.
Use fictional patient data. It has no login, authorization, audit trail, encryption
configuration, backup workflow, or detailed phone/name validation. Credentials are
inline only to match your beginner project: do not commit real passwords to Git.
The default root account is retained for local setup; use a restricted database
account for a real deployment. Database calls currently run on the JavaFX UI
thread, so slow queries can temporarily freeze the interface. A larger version
should move them into background JavaFX Tasks.

The rewritten source has not been compiled or run against your JavaFX/MySQL
installation. Dependency versions and your existing module/build configuration
were not supplied. No claim of runtime testing is made.
