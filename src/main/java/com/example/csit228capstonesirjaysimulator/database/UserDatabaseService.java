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

    public UserProfile getCurrentUser()                      { return currentUser; }
    public void        setCurrentUser(UserProfile u)         { this.currentUser = u; }

    public boolean registerUser(String teacherId, String username,
                                String course, String section, String password) {
        String sqlUser  = "INSERT INTO users (teacher_id, username, course, section, password) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlStats = "INSERT INTO stats (teacher_id) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stUser  = conn.prepareStatement(sqlUser);
                 PreparedStatement stStats = conn.prepareStatement(sqlStats)) {

                stUser.setString(1, teacherId);
                stUser.setString(2, username);
                stUser.setString(3, course);
                stUser.setString(4, section);
                stUser.setString(5, password);
                stUser.executeUpdate();

                stStats.setString(1, teacherId);
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

    public boolean updateUser(String teacherId, String newUsername,
                              String newCourse, String newSection, String newPassword) {
        String sql = "UPDATE users SET username = ?, course = ?, section = ?, password = ? "
                + "WHERE teacher_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, newUsername);
            st.setString(2, newCourse);
            st.setString(3, newSection);
            st.setString(4, newPassword);
            st.setString(5, teacherId);
            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<UserProfile> getAllProfiles() {
        List<UserProfile> list = new ArrayList<>();
        String sql = "SELECT teacher_id, username, course, section, password "
                + "FROM users ORDER BY last_played DESC, created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                list.add(new UserProfile(
                        rs.getString("teacher_id"),
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


    public void updateLastPlayed(String teacherId) {
        String sql = "UPDATE users SET last_played = CURRENT_TIMESTAMP WHERE teacher_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, teacherId);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UserProfile loginUser(String teacherId, String password) {
        String sql = "SELECT * FROM users WHERE teacher_id = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, teacherId);
            st.setString(2, password);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new UserProfile(
                            rs.getString("teacher_id"),
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

    public boolean deleteUser(String teacherId) {
        String sql = "DELETE FROM users WHERE teacher_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, teacherId);
            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public synchronized void saveSession(String teacherId, Sessionstats ss, List<Mission<?>> missions) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                insertSession(conn, teacherId, ss);
                upsertStats(conn, teacherId, ss);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        MissionRepository.getInstance().saveMissionProgress(teacherId, missions);
    }

    private void insertSession(Connection conn, String teacherId, Sessionstats ss)
            throws SQLException {
        String sql = "INSERT INTO sessions "
                + "(teacher_id, final_score, cheaters_caught, false_accusations, "
                + " highest_combo, total_attempts, total_correct, duration_seconds) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, teacherId);
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

    private void upsertStats(Connection conn, String teacherId, Sessionstats ss)
            throws SQLException {
        String sql = """
                INSERT INTO stats
                    (teacher_id, cheaters_caught, false_accusations,
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
            st.setString(1, teacherId);
            st.setInt(2, ss.getCheatersCaught());
            st.setInt(3, ss.getFalseAccusations());
            st.setInt(4, ss.getHighestCombo());
            st.setInt(5, ss.getTotalAttempts());
            st.setInt(6, ss.getTotalCorrect());
            st.setDouble(7, ss.getDurationSeconds() / 3600.0);
            st.setDouble(8, ss.getCatchAccuracy() * 100.0);
            st.executeUpdate();
        }
    }


    public double getCatchAccuracy(String teacherId)   { return queryDouble(teacherId, "catch_accuracy");    }
    public int    getHighestCombo(String teacherId)    { return queryInt(teacherId, "highest_combo");        }
    public int    getCheatersCaught(String teacherId)  { return queryInt(teacherId, "cheaters_caught");      }
    public int    getFalseAccusations(String teacherId){ return queryInt(teacherId, "false_accusations");    }
    public int    getQuizzesConducted(String teacherId){ return queryInt(teacherId, "quizzes_conducted");    }
    public double getHoursWorked(String teacherId)     { return queryDouble(teacherId, "hours_worked");      }


    public List<ScoreRecord> getLeaderboard() {
        List<ScoreRecord> list = new ArrayList<>();
        String sql = "SELECT u.username, MAX(s.final_score) AS high_score "
                + "FROM sessions s "
                + "JOIN users u ON u.teacher_id = s.teacher_id "
                + "GROUP BY u.teacher_id "
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

    private int queryInt(String teacherId, String column) {
        String sql = "SELECT " + column + " FROM stats WHERE teacher_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, teacherId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private double queryDouble(String teacherId, String column) {
        String sql = "SELECT " + column + " FROM stats WHERE teacher_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, teacherId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }
}