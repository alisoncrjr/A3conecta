package Utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    private static HikariDataSource dataSource;
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());

    static {

    }

    public static Connection getConnection() throws SQLException {
        initDataSource();
        if (dataSource == null) throw new SQLException("DataSource not initialized");
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Error closing DataSource", e);
            } finally {
                dataSource = null;
            }
        }
    }

    /**
     * Execute SQL statements from a resource file (simple splitting by ';').
     * Use for schema creation or migrations. Resource path is relative to classpath, e.g. '/db/schema.sql'
     */
    public static void runSqlScriptFromResource(String resourcePath) throws SQLException {
        try (java.io.InputStream in = Database.class.getResourceAsStream(resourcePath)) {
            if (in == null) throw new IllegalArgumentException("Resource not found: " + resourcePath);
            String sql = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            List<String> statements = splitSqlStatements(sql);
            try (Connection conn = getConnection()) {
                boolean previousAuto = conn.getAutoCommit();
                try {
                    conn.setAutoCommit(false);
                    for (String stmt : statements) {
                        String s = stmt.trim();
                        if (s.isEmpty()) continue;
                        try (Statement st = conn.createStatement()) {
                            st.execute(s);
                        }
                    }
                    conn.commit();
                } catch (Exception ex) {
                    try { conn.rollback(); } catch (Exception ignore) {}
                    throw ex;
                } finally {
                    try { conn.setAutoCommit(previousAuto); } catch (Exception ignore) {}
                }
            }
        } catch (IOException e) {
            throw new SQLException("Failed to read SQL resource: " + e.getMessage(), e);
        }
    }

    /**
     * Naive SQL splitter that respects single/double quotes and comments.
     * It returns a list of SQL statements without the trailing semicolons.
     */
    private static List<String> splitSqlStatements(String sql) {
        List<String> out = new ArrayList<>();
        if (sql == null || sql.isBlank()) return out;

        StringBuilder sb = new StringBuilder();
        boolean inSingle = false;
        boolean inDouble = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;

        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            char next = i + 1 < sql.length() ? sql.charAt(i + 1) : '\0';


            if (!inSingle && !inDouble && !inBlockComment && c == '-' && next == '-') {
                inLineComment = true;
                i++;
                continue;
            }
            if (!inSingle && !inDouble && !inBlockComment && c == '#') {
                inLineComment = true;
                continue;
            }


            if (!inSingle && !inDouble && !inLineComment && !inBlockComment && c == '/' && next == '*') {
                inBlockComment = true;
                i++;
                continue;
            }


            if (inBlockComment && c == '*' && next == '/') {
                inBlockComment = false;
                i++;
                continue;
            }


            if (inLineComment && (c == '\n' || c == '\r')) {
                inLineComment = false;

                continue;
            }

            if (inLineComment || inBlockComment) {
                continue;
            }


            if (c == '\'' && !inDouble) {
                inSingle = !inSingle;
                sb.append(c);
                continue;
            }
            if (c == '"' && !inSingle) {
                inDouble = !inDouble;
                sb.append(c);
                continue;
            }


            if (c == ';' && !inSingle && !inDouble) {
                String statement = sb.toString().trim();
                if (!statement.isEmpty()) out.add(statement);
                sb.setLength(0);
                continue;
            }

            sb.append(c);
        }

        String last = sb.toString().trim();
        if (!last.isEmpty()) out.add(last);
        return out;
    }


    private static synchronized void initDataSource() {
        try {
            if (dataSource != null && !dataSource.isClosed()) return;
        } catch (Exception ignored) {}

        Properties props = new Properties();
        try (InputStream in = Database.class.getResourceAsStream("/db/db.properties")) {
            if (in != null) props.load(in);
        } catch (IOException e) {
            LOGGER.log(Level.FINE, "No db.properties or failed to load it: {0}", e.getMessage());
        }

        String host = System.getenv().getOrDefault("DB_HOST", props.getProperty("db.host", "localhost"));
        String port = System.getenv().getOrDefault("DB_PORT", props.getProperty("db.port", "3306"));
        String database = System.getenv().getOrDefault("DB_NAME", props.getProperty("db.name", "conecta"));
        String user = System.getenv().getOrDefault("DB_USER", props.getProperty("db.user", "root"));
        String pass = System.getenv().getOrDefault("DB_PASS", props.getProperty("db.pass", "123456"));
        String poolSize = System.getenv().getOrDefault("DB_POOL_SIZE", props.getProperty("db.pool.size", "10"));

        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true", host, port, database);

        try {

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException cnf) {
                LOGGER.log(Level.WARNING, "MySQL driver class not found: {0}", cnf.getMessage());
                throw new SQLException("MySQL driver not found on classpath", cnf);
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(user);
            config.setPassword(pass);
            config.setMaximumPoolSize(Integer.parseInt(poolSize));
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            HikariDataSource ds = new HikariDataSource(config);
            dataSource = ds;
            LOGGER.log(Level.INFO, "DataSource initialized: {0}", jdbcUrl);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to initialize DataSource: {0}", e.getMessage());
            LOGGER.log(Level.FINE, "DataSource init exception", e);
            dataSource = null;
        }
    }

    public static boolean isEnabled() {

        Properties props = new Properties();
        try (InputStream in = Database.class.getResourceAsStream("/db/db.properties")) {
            if (in != null) props.load(in);
        } catch (IOException e) {

        }
        String envFlag = System.getenv().getOrDefault("USE_DB", props.getProperty("db.enabled", "false"));
        return Boolean.parseBoolean(envFlag);
    }
}
