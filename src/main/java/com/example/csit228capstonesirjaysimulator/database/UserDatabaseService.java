package com.example.csit228capstonesirjaysimulator.database;

import java.sql.*;
import static com.example.csit228capstonesirjaysimulator.database.DatabaseConnection.getConnection;

public class UserDatabaseService {

    private static UserDatabaseService instance;

    private UserDatabaseService() {}

    public static synchronized UserDatabaseService getInstance() {
        if (instance == null) {
            instance = new UserDatabaseService();
        }
        return instance;
    }

    public static class User {
        private String username;
        private int highScore;

        public User(String username, int highScore) {
            this.username = username;
            this.highScore = highScore;
        }

        public String getUsername() { return username; }
        public int getHighScore() { return highScore; }
    }


    public boolean registerUser(String username, String email, String password) {
        String query = "INSERT INTO users (username, email, password, created_at) VALUES (?, ?, ?, NOW())";


        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, username);
            st.setString(2, password);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    int highScore = rs.getInt("high_score");
                    return new User(username, highScore);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}