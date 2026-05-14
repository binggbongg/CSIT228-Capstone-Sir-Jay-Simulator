package com.example.csit228capstonesirjaysimulator.database;

import com.example.csit228capstonesirjaysimulator.entity.ScoreRecord;
import com.example.csit228capstonesirjaysimulator.component.mission.Mission;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDatabaseService {

    private static UserDatabaseService instance;
    private UserProfile currentUser;

    private UserDatabaseService(UserProfile currentUser) {
        this.currentUser = currentUser;
    }

    public static synchronized UserDatabaseService getInstance() {
        if (instance == null) instance = new UserDatabaseService(null);
        return instance;
    }

    public UserProfile getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserProfile currentUser) {
        this.currentUser = currentUser;
    }

    public boolean registerUser(String studentId, String username,
                                String course, String section, String password) {
        String sqlUser  = "INSERT INTO users (student_id, username, course, section, password) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlStats = "INSERT INTO stats (student_id) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
          conn.setAutoCommit(false);

            try (PreparedStatement stUser  = conn.prepareStatement(sqlUser);
                 PreparedStatement stStats = conn.prepareStatement(sqlStats)) {

                stUser.setString(1, studentId);
                stUser.setString(2, username);
                stUser.setString(3, course);
                stUser.setString(4, section);
                stUser.setString(5, password);
                stUser.executeUpdate();

                stStats.setString(1, studentId);
                stStats.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<UserProfile> getAllProfiles() {
        List<UserProfile> list = new ArrayList<>();
        String sql = "SELECT student_id, username, course, section, password "
                + "FROM users ORDER BY username";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                list.add(new UserProfile(
                        rs.getString("student_id"),
                        rs.getString("username"),
                        rs.getString("course"),
                        rs.getString("section"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public UserProfile loginUser(String studentId, String password) {
        String sql = "SELECT * FROM users WHERE student_id = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, studentId);
            st.setString(2, password);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new UserProfile(
                            rs.getString("student_id"),
                            rs.getString("username"),
                            rs.getString("course"),
                            rs.getString("section"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveProfile(UserProfile profile) {
        String sql = "INSERT INTO users (student_id, username, course, section, password) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "username = VALUES(username), "
                + "course = VALUES(course),   "
                + "section = VALUES(section),  "
                + "password = VALUES(password)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, profile.getStudentId());
            st.setString(2, profile.getUsername());
            st.setString(3, profile.getCourse());
            st.setString(4, profile.getSection());
            st.setString(5, profile.getPassword());
            st.executeUpdate();

            String sqlStats = "INSERT IGNORE INTO stats (student_id) VALUES (?)";
            try (PreparedStatement stStats = conn.prepareStatement(sqlStats)) {
                stStats.setString(1, profile.getStudentId());
                stStats.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized void saveSession(String studentId, Sessionstats ss, List<Mission<?>> missions) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                insertSession(conn, studentId, ss);
                upsertStats(conn, studentId, ss);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }


        MissionRepository.getInstance().saveMissionProgress(studentId, missions);
    }

    public boolean deleteUser(String studentId) {
        String sql = "DELETE FROM users WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, studentId);
            int affected = st.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void insertSession(Connection conn, String studentId, Sessionstats ss)
            throws SQLException {
        String sql = "INSERT INTO sessions "
                + "(student_id, final_score, cheaters_caught, false_accusations, "
                + " highest_combo, total_attempts, total_correct, duration_seconds) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, studentId);
            st.setInt(2, ss.getFinalScore());
            st.setInt(3, ss.getCheatersCaught());
            st.setInt(4, ss.getFalseAccusations());
            st.setInt(5, ss.getHighestCombo());
            st.setInt(6, ss.getTotalAttempts());
            st.setInt(7, ss.getTotalCorrect());
            st.setDouble(8, ss.getDurationSeconds());
            st.executeUpdate();
        }
    }

    private void upsertStats(Connection conn, String studentId, Sessionstats ss)
            throws SQLException {
        String sql = """
                INSERT INTO stats
                    (student_id, cheaters_caught, false_accusations,
                     highest_combo, total_attempts, total_correct,
                     hours_worked, catch_accuracy, quizzes_conducted)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)
                ON DUPLICATE KEY UPDATE
                    cheaters_caught   = cheaters_caught   + VALUES(cheaters_caught),
                    false_accusations = false_accusations + VALUES(false_accusations),
                    highest_combo     = GREATEST(highest_combo, VALUES(highest_combo)),
                    total_attempts    = total_attempts    + VALUES(total_attempts),
                    total_correct     = total_correct     + VALUES(total_correct),
                    hours_worked      = hours_worked      + VALUES(hours_worked),
                    catch_accuracy    = IF(total_attempts + VALUES(total_attempts) = 0, 0,
                                         (total_correct + VALUES(total_correct)) * 100.0
                                         / (total_attempts + VALUES(total_attempts))),
                    quizzes_conducted = quizzes_conducted + 1
                """;

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, studentId);
            st.setInt(2, ss.getCheatersCaught());
            st.setInt(3, ss.getFalseAccusations());
            st.setInt(4, ss.getHighestCombo());
            st.setInt(5, ss.getTotalAttempts());
            st.setInt(6, ss.getTotalCorrect());
            st.setDouble(7, ss.getDurationSeconds() / 3600.0);   // seconds → hours
            st.setDouble(8, ss.getCatchAccuracy() * 100.0);
            st.executeUpdate();
        }
    }

    public double getCatchAccuracy(String studentId) {
        return queryDouble(studentId, "catch_accuracy");
    }

    public int getHighestCombo(String studentId) {
        return queryInt(studentId, "highest_combo");
    }

    public int getCheatersCaught(String studentId) {
        return queryInt(studentId, "cheaters_caught");
    }

    public int getFalseAccusations(String studentId) {
        return queryInt(studentId, "false_accusations");
    }

    public int getQuizzesConducted(String studentId) {
        return queryInt(studentId, "quizzes_conducted");
    }

    public double getHoursWorked(String studentId) {
        return queryDouble(studentId, "hours_worked");
    }


    public List<ScoreRecord> getLeaderboard() {
        List<ScoreRecord> list = new ArrayList<>();
        String sql = "SELECT u.username, MAX(s.final_score) AS high_score "
                + "FROM sessions s "
                + "JOIN users u ON u.student_id = s.student_id "
                + "GROUP BY u.student_id "
                + "ORDER BY high_score DESC "
                + "LIMIT 10";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                list.add(new ScoreRecord(
                        rs.getString("username"),
                        rs.getInt("high_score")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    private int queryInt(String studentId, String column) {
        String sql = "SELECT " + column + " FROM stats WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, studentId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private double queryDouble(String studentId, String column) {
        String sql = "SELECT " + column + " FROM stats WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, studentId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }
}