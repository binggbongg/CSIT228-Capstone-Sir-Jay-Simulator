package com.example.csit228capstonesirjaysimulator.database;

import com.example.csit228capstonesirjaysimulator.component.mission.Mission;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MissionRepository {

    private static MissionRepository instance;
    private MissionRepository() {}
    public static synchronized MissionRepository getInstance() {
        if (instance == null) instance = new MissionRepository();
        return instance;
    }

    public List<Mission<?>> loadAllMissions(String teacherId) {
        List<Mission<?>> list = new ArrayList<>();

        String sql = "SELECT m.id, m.description, m.type, m.target_value, " +
                "mp.current_value, mp.completed " +
                "FROM missions m " +
                "LEFT JOIN mission_progress mp ON m.id = mp.mission_id AND mp.teacher_id = ? " +
                "ORDER BY m.id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, teacherId);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String desc = rs.getString("description");
                    String type = rs.getString("type");
                    int target = rs.getInt("target_value");

                    int currentProgress = rs.getInt("current_value");
                    boolean isCompleted = rs.getBoolean("completed");

                    if ("BOOLEAN".equalsIgnoreCase(type)) {
                        list.add(new Mission<>(id, desc, Boolean.TRUE, isCompleted));
                    } else {
                        list.add(new Mission<>(id, desc, target, currentProgress));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void saveMissionProgress(String teacherId, List<Mission<?>> missions) {
        String sql = "INSERT INTO mission_progress "
                + "(teacher_id, mission_id, current_value, completed) "
                + "VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "current_value = VALUES(current_value), "
                + "completed     = VALUES(completed), "
                + "session_date  = CURRENT_TIMESTAMP";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            for (Mission<?> m : missions) {
                st.setString(1, teacherId);
                st.setInt(2, m.getMissionId());

                Object val   = m.getCurrent();
                int    dbVal = (val instanceof Boolean) ? ((Boolean) val ? 1 : 0) : (Integer) val;

                st.setInt(3, dbVal);
                st.setBoolean(4, m.isCompleted());
                st.addBatch();
            }
            st.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<MissionProgressRow> getLatestSessionProgress(String teacherId) {
        List<MissionProgressRow> rows = new ArrayList<>();

        String sql = """
                SELECT m.id, m.description, m.type, m.target_value, mp.current_value, mp.completed
                FROM missions m
                LEFT JOIN mission_progress mp
                       ON mp.mission_id = m.id AND mp.teacher_id = ? AND mp.session_date = (
                              SELECT MAX(mp2.session_date)
                              FROM mission_progress mp2
                              WHERE mp2.teacher_id = ? AND mp2.mission_id = m.id)
                ORDER BY m.id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, teacherId);
            st.setString(2, teacherId);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    rows.add(new MissionProgressRow(
                            rs.getInt("id"),
                            rs.getString("description"),
                            rs.getString("type"),
                            rs.getInt("target_value"),
                            rs.getInt("current_value"),
                            rs.getBoolean("completed")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public record MissionProgressRow(
            int missionId,
            String description,
            String type,
            int targetValue,
            int currentValue,
            boolean completed
    ) {
        public String progressText() {
            if ("BOOLEAN".equalsIgnoreCase(type)) return completed ? "/" : "X";
            return currentValue + " / " + targetValue;
        }
    }
}