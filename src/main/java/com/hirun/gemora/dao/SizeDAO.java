package com.hirun.gemora.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import com.hirun.gemora.DatabaseConnection;

public class SizeDAO {

    // Add a new size
    public static void addSize(String sizeName) throws SQLException {
        String sql = "INSERT INTO Size (name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sizeName);
            stmt.executeUpdate();
        }
    }

    // Get size by name
    public static JSONObject getSizeByName(String sizeName) throws SQLException {
        String sql = "SELECT size_id, name FROM Size WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sizeName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("size_id", rs.getInt("size_id"));
                    json.put("name", rs.getString("name"));
                    return json;
                }
            }
        }
        return null;
    }

    // Get all sizes
    public static List<JSONObject> getAllSizes() throws SQLException {
        List<JSONObject> sizes = new ArrayList<>();
        String sql = "SELECT size_id, name FROM Size";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("size_id", rs.getInt("size_id"));
                json.put("name", rs.getString("name"));
                sizes.add(json);
            }
        }
        return sizes;
    }

    // Delete size by ID
    public static void deleteSize(String sizeName) throws SQLException {
        String sql = "DELETE FROM Size WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sizeName);
            stmt.executeUpdate();
        }
    }

    // Check if a size exists by name
    public static boolean doesSizeExist(String sizeName) throws SQLException {
        String sql = "SELECT 1 FROM Size WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sizeName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static String[] getAllSizeNames() throws SQLException {
        String sql = "SELECT name FROM Size";
        List<String> sizeNames = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sizeNames.add(rs.getString("name"));
            }
        }

        // Convert list to array
        return sizeNames.toArray(String[]::new);
    }
}
