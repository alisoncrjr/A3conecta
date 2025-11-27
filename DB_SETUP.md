# MySQL Integration Setup (Conecta)

This project is prepared to use MySQL via HikariCP. Follow these steps to enable and run the database-backed mode.

1) Install MySQL
- Install MySQL Server (8.x) and create a database (example `conecta`).

2) Configure connection
- You can either edit `src/main/resources/db/db.properties` or provide environment variables.
- Supported env vars:
  - `DB_HOST` (default: `localhost`)
  - `DB_PORT` (default: `3306`)
  - `DB_NAME` (default: `conecta`)
  - `DB_USER` (default: `root`)
  - `DB_PASS` (default: ``)
  - `DB_POOL_SIZE` (default: `10`)

3) Create schema
- The SQL schema is in `src/main/resources/db/schema.sql`.
- You can execute it manually using the MySQL client:

```powershell
mysql -u root -p
CREATE DATABASE conecta CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE conecta;
SOURCE src/main/resources/db/schema.sql;
```

4) Run app in DB mode
- Set env var `USE_DB=true` and ensure DB credentials are set, then run:

```powershell
$env:USE_DB = 'true'
$env:DB_HOST = 'localhost'
$env:DB_PORT = '3306'
$env:DB_NAME = 'conecta'
$env:DB_USER = 'root'
$env:DB_PASS = 'yourpassword'
mvn -DskipTests javafx:run
```

When `USE_DB=true` the application attempts to run the schema resource on startup (it will execute the `schema.sql` statements). For production you might want to use a migration tool such as Flyway or Liquibase instead.

5) DAOs and next steps
- I added a `Utils.Database` helper and an example `dao/UserDAO` with a lightweight `UserRecord` returned from queries.
- You should implement DAOs for `ProjetoConstrucao`, `Contrato`, `ItemOrcamento`, `Avaliacao`, `Conversa`, and `Mensagem` following `UserDAO` patterns.
- Optionally, you can migrate `Application.App` to read from DB instead of in-memory lists.

If you'd like, I can:
- Add full DAOs for all domain models and wire the app to use them;
- Add Flyway migrations instead of ad-hoc schema execution;
- Replace seedData with DB seeding and login/auth backed by DB.

Tell me which next step you want me to implement first.