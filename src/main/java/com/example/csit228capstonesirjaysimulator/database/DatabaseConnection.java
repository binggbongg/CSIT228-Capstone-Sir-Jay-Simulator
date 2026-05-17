package com.example.csit228capstonesirjaysimulator.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String BASE_URL = "jdbc:mysql://localhost:3306";
    private static final String DB_URL   = "jdbc:mysql://localhost:3306/seratosimulator";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void initialize(){
        System.out.println("Initializing database....");

        try{
            createDatabaseIfMissing();
            try (Connection conn = getConnection();
                 Statement st   = conn.createStatement()) {

                createTables(st);
                seedMissions(st);
                System.out.println("Database ready.");
            }
        }catch(SQLException e){
            System.out.println("Initialization failed: "+ e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createDatabaseIfMissing() throws SQLException {
        try (Connection conn = DriverManager.getConnection(BASE_URL, USERNAME, PASSWORD);
             Statement  st   = conn.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS seratosimulator");
        }
    }

    private static void createTables(Statement st) throws SQLException {

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS users (
                teacher_id VARCHAR(20) NOT NULL PRIMARY KEY,
                username VARCHAR(60) NOT NULL,
                course VARCHAR(20) NOT NULL,
                section VARCHAR(10) NOT NULL,
                password VARCHAR(255) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS stats (
                teacher_id VARCHAR(20) NOT NULL PRIMARY KEY,
                catch_accuracy DOUBLE NOT NULL DEFAULT 0.0,
                highest_combo INT NOT NULL DEFAULT 0,
                cheaters_caught INT NOT NULL DEFAULT 0,
                false_accusations INT NOT NULL DEFAULT 0,
                quizzes_conducted INT NOT NULL DEFAULT 0,
                hours_worked DOUBLE NOT NULL DEFAULT 0.0,
                total_attempts INT NOT NULL DEFAULT 0,
                total_correct INT NOT NULL DEFAULT 0,
                CONSTRAINT fk_stats_user FOREIGN KEY (teacher_id) REFERENCES users(teacher_id) ON DELETE CASCADE
            )
        """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS missions (
                id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                description VARCHAR(255) NOT NULL UNIQUE,
                type ENUM('INTEGER','BOOLEAN') NOT NULL,
                target_value INT NOT NULL
            )
        """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS mission_progress (
                id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                teacher_id VARCHAR(20) NOT NULL,
                mission_id INT NOT NULL,
                session_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                current_value INT NOT NULL DEFAULT 0,
                completed TINYINT(1) NOT NULL DEFAULT 0,
                CONSTRAINT fk_mp_user FOREIGN KEY (teacher_id) REFERENCES users(teacher_id) ON DELETE CASCADE,
                CONSTRAINT fk_mp_mission FOREIGN KEY (mission_id) REFERENCES missions(id),
                CONSTRAINT uq_mp UNIQUE (teacher_id, mission_id)
            )
        """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS sessions (
                id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                teacher_id VARCHAR(20) NOT NULL,
                played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                final_score INT NOT NULL DEFAULT 0,
                cheaters_caught INT NOT NULL DEFAULT 0,
                false_accusations INT NOT NULL DEFAULT 0,
                highest_combo INT NOT NULL DEFAULT 0,
                total_attempts INT NOT NULL DEFAULT 0,
                total_correct INT NOT NULL DEFAULT 0,
                duration_seconds DOUBLE NOT NULL DEFAULT 0.0,
                CONSTRAINT fk_session_user FOREIGN KEY (teacher_id) REFERENCES users(teacher_id) ON DELETE CASCADE
            )
        """);
    }

    private static void seedMissions(Statement st) throws SQLException {
        String[] seeds = {
                "('Catch 3 cheaters in one session',          'INTEGER', 3)",
                "('Catch 5 cheaters in one session',          'INTEGER', 5)",
                "('Reach a streak of 5',                      'INTEGER', 5)",
                "('Reach a streak of 10',                     'INTEGER', 10)",
                "('Resolve a distractor 2 times',             'INTEGER', 2)",
                "('Score over 5000 in one session',           'INTEGER', 5000)",
                "('Score over 10000 in one session',          'INTEGER', 10000)",
                "('Achieve a score multiplier of 3x',         'BOOLEAN', 1)",
                "('Achieve a score multiplier of 5x',         'BOOLEAN', 1)",
        };

        for (String row : seeds) {
            st.executeUpdate(
                    "INSERT IGNORE INTO missions (description, type, target_value) VALUES " + row
            );
        }
    }
}