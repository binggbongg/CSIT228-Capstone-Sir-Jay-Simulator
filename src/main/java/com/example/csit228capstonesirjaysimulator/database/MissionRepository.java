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

    public List<Mission<?>> loadGlobalMissions() {
        List<Mission<?>> list = new ArrayList<>();
        String sql = "SELECT id, description, type, target_value FROM missions ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                int    id     = rs.getInt("id");
                String desc   = rs.getString("description");
                String type   = rs.getString("type");
                int    target = rs.getInt("target_value");

                if ("BOOLEAN".equalsIgnoreCase(type)) {
                    list.add(new Mission<>(id, desc, Boolean.TRUE, Boolean.FALSE));
                } else {
                    list.add(new Mission<>(id, desc, target, 0));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void saveMissionProgress(String studentId, List<Mission<?>> missions) {
        String sql = "INSERT INTO mission_progress "
                + "(student_id, mission_id, current_value, completed) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            for (Mission<?> m : missions) {
                st.setString(1, studentId);
                st.setInt(2, m.getMissionId());

                // Normalise generic value → int for DB storage
                Object val = m.getCurrent();
                int    dbVal = (val instanceof Boolean)
                        ? ((Boolean) val ? 1 : 0)
                        : (Integer) val;

                st.setInt(3, dbVal);
                st.setBoolean(4, m.isCompleted());
                st.addBatch();
            }
            st.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<MissionProgressRow> getLatestSessionProgress(String studentId) {
        List<MissionProgressRow> rows = new ArrayList<>();


        String sql = """
                SELECT m.id, m.description, m.type, m.target_value,
                       mp.current_value, mp.completed
                FROM   missions m
                LEFT JOIN mission_progress mp
                       ON mp.mission_id  = m.id
                      AND mp.student_id  = ?
                      AND mp.session_date = (
                              SELECT MAX(mp2.session_date)
                              FROM   mission_progress mp2
                              WHERE  mp2.student_id = ?
                                AND  mp2.mission_id = m.id
                          )
                ORDER BY m.id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, studentId);
            st.setString(2, studentId);

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
            int     missionId,
            String  description,
            String  type,
            int     targetValue,
            int     currentValue,
            boolean completed
    ) {
        public String progressText() {
            if ("BOOLEAN".equalsIgnoreCase(type)) return completed ? "/" : "X";
            return currentValue + " / " + targetValue;
        }
    }
}