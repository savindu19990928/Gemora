package com.hirun.gemora.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import com.hirun.gemora.DatabaseConnection;

public class ProductLotAdditionalCostDAO {

    // Add a new additional cost entry for a product lot
    public static void addProductLotAdditionalCost(int lotId, String title, String description, double costAmount) throws SQLException {
        String sql = "INSERT INTO ProductLotAdditionalCost (lot_id, title, cost_description, cost_amount) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lotId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setDouble(4, costAmount);
            stmt.executeUpdate();
        }
    }

    // Update an existing additional cost entry for a product lot
    public static void updateProductLotAdditionalCost(int costId, int lotId, String title, String description, double costAmount) throws SQLException {
        String sql = "UPDATE ProductLotAdditionalCost SET lot_id = ?, title = ?, cost_description = ?, cost_amount = ? WHERE cost_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lotId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setDouble(4, costAmount);
            stmt.setInt(5, costId);
            stmt.executeUpdate();
        }
    }

    // Delete a product lot additional cost entry by cost_id
    public static void deleteProductLotAdditionalCost(int costId) throws SQLException {
        String sql = "DELETE FROM ProductLotAdditionalCost WHERE cost_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, costId);
            stmt.executeUpdate();
        }
    }

    // Get all additional costs for a specific product lot
    public static List<JSONObject> getProductLotAdditionalCosts(int lotId) throws SQLException {
        List<JSONObject> costs = new ArrayList<>();
        String sql = "SELECT cost_id, lot_id, title, cost_description, cost_amount FROM ProductLotAdditionalCost WHERE lot_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lotId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("cost_id", rs.getInt("cost_id"));
                    json.put("lot_id", rs.getInt("lot_id"));
                    json.put("title", rs.getString("title"));
                    json.put("cost_description", rs.getString("cost_description"));
                    json.put("cost_amount", rs.getDouble("cost_amount"));
                    costs.add(json);
                }
            }
        }
        return costs;
    }

    // Get a specific additional cost by cost_id
    public static JSONObject getProductLotAdditionalCostById(int costId) throws SQLException {
        String sql = "SELECT cost_id, lot_id, title, cost_description, cost_amount FROM ProductLotAdditionalCost WHERE cost_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, costId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("cost_id", rs.getInt("cost_id"));
                    json.put("lot_id", rs.getInt("lot_id"));
                    json.put("title", rs.getString("title"));
                    json.put("cost_description", rs.getString("cost_description"));
                    json.put("cost_amount", rs.getDouble("cost_amount"));
                    return json;
                }
            }
        }
        return null;
    }
}
