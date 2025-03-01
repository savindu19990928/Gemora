package com.hirun.gemora.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import com.hirun.gemora.DatabaseConnection;

public class ColorDAO {

    // Add a new color
    public static void addColor(String colorName) throws SQLException {
        String sql = "INSERT INTO Color (name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, colorName);
            stmt.executeUpdate();
        }
    }

    // Get color by name
    public static JSONObject getColorByName(String colorName) throws SQLException {
        String sql = "SELECT color_id, name FROM Color WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, colorName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("color_id", rs.getInt("color_id"));
                    json.put("name", rs.getString("name"));
                    return json;
                }
            }
        }
        return null;
    }

    // Get all colors
    public static List<JSONObject> getAllColors() throws SQLException {
        List<JSONObject> colors = new ArrayList<>();
        String sql = "SELECT color_id, name FROM Color";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("color_id", rs.getInt("color_id"));
                json.put("name", rs.getString("name"));
                colors.add(json);
            }
        }
        return colors;
    }

    // Delete color by ID
    public static void deleteColor(String colorName) throws SQLException {
        String sql = "DELETE FROM Color WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, colorName);
            stmt.executeUpdate();
        }
    }

    // Check if a color exists by name
    public static boolean doesColorExist(String colorName) throws SQLException {
        String sql = "SELECT 1 FROM Color WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, colorName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static String[] getAllColorNames() throws SQLException {
        String sql = "SELECT name FROM Color";
        List<String> colorNames = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                colorNames.add(rs.getString("name"));
            }
        }

        // Convert list to array
        return colorNames.toArray(String[]::new);
    }
}
