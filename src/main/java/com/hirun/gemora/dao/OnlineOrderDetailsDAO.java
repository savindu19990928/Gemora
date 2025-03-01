package com.hirun.gemora.dao;

import org.json.JSONObject;
import com.hirun.gemora.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OnlineOrderDetailsDAO {

    // Add an online order detail
    public static void addOnlineOrderDetail(int customerId, int lotId, int quantity, double soldPrice) throws SQLException {
        String sql = "INSERT INTO OnlineOrderDetails (customer_id, lot_id, quantity, sold_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, lotId);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, soldPrice);
            stmt.executeUpdate();
        }
    }

    // Update an existing online order detail
    public static int updateOnlineOrderDetail(int orderId, int customerId, int lotId, int quantity, double soldPrice) throws SQLException {
        String sql = "UPDATE OnlineOrderDetails SET customer_id = ?, lot_id = ?, quantity = ?, sold_price = ? WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, lotId);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, soldPrice);
            stmt.setInt(5, orderId);

            // Execute the update and return the number of affected rows
            return stmt.executeUpdate();
        }
    }

    // Delete an online order detail by order_id
    public static int deleteOnlineOrderDetail(int orderId) throws SQLException {
        String sql = "DELETE FROM OnlineOrderDetails WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            return stmt.executeUpdate();
        }
    }

    // Get all online order details for a specific customer
    public static List<JSONObject> getOnlineOrderDetailsByCustomer(int customerId) throws SQLException {
        List<JSONObject> orderDetails = new ArrayList<>();
        String sql = "SELECT order_id, customer_id, lot_id, quantity, sold_price FROM OnlineOrderDetails WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("order_id", rs.getInt("order_id"));
                    json.put("customer_id", rs.getInt("customer_id"));
                    json.put("lot_id", rs.getInt("lot_id"));
                    json.put("quantity", rs.getInt("quantity"));
                    json.put("sold_price", rs.getDouble("sold_price"));
                    orderDetails.add(json);
                }
            }
        }
        return orderDetails;
    }

    // Get an online order detail by order_id
    public static JSONObject getOnlineOrderDetailById(int orderId) throws SQLException {
        String sql = "SELECT order_id, customer_id, lot_id, quantity, sold_price FROM OnlineOrderDetails WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("order_id", rs.getInt("order_id"));
                    json.put("customer_id", rs.getInt("customer_id"));
                    json.put("lot_id", rs.getInt("lot_id"));
                    json.put("quantity", rs.getInt("quantity"));
                    json.put("sold_price", rs.getDouble("sold_price"));
                    return json;
                }
            }
        }
        return null;
    }
}
