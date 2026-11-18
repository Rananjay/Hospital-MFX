# Hospital-MFX

This project has been consolidated into the [`java-projects`](https://github.com/Rananjay/java-projects) repository.

## Maintained location

The active version of this Hospital Management System is now maintained at:

https://github.com/Rananjay/java-projects/tree/main/hospital-mfx

## What changed

- The application is now a Maven module named `hospital-mfx`.
- The original `application` and `com.hospital` package structure is preserved.
- MySQL Connector/J is now managed through Maven instead of the bundled JAR in `lib/`.
- Database setup instructions and the `database.sql` script are included in the consolidated module.

## Run the maintained version

```bash
git clone https://github.com/Rananjay/java-projects.git
cd java-projects
mysql -u root -p < hospital-mfx/database.sql
export DB_USER=your_mysql_user
export DB_PASSWORD=your_mysql_password
mvn --projects hospital-mfx javafx:run
```

This repository is retained for history and to preserve its original URL.