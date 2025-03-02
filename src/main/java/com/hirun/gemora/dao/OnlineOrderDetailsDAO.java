package com.hirun.gemora.dao;

import org.json.JSONObject;
import com.hirun.gemora.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OnlineOrderDetailsDAO {

    // Add an online order detail
    public static void addOnlineOrderDetail(int customerId, int lotId, int quantity, double soldPrice) throws SQLException {
        // Start a transaction to handle both operations (insert and update product lot)
        String insertSql = "INSERT INTO OnlineOrderDetails (customer_id, lot_id, quantity, sold_price) VALUES (?, ?, ?, ?)";
        String updateLotSql = "UPDATE ProductLot SET quantity = quantity - ? WHERE lot_id = ? AND quantity >= ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start a transaction
            conn.setAutoCommit(false);

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql); PreparedStatement updateLotStmt = conn.prepareStatement(updateLotSql)) {

                // Check if enough quantity is available in ProductLot
                updateLotStmt.setInt(1, quantity);  // Quantity to reduce
                updateLotStmt.setInt(2, lotId);  // ProductLot ID
                updateLotStmt.setInt(3, quantity);  // Ensure quantity is sufficient

                // Execute the update on ProductLot
                int rowsUpdated = updateLotStmt.executeUpdate();

                if (rowsUpdated > 0) {
                    // If the quantity is sufficient, insert the order
                    insertStmt.setInt(1, customerId);
                    insertStmt.setInt(2, lotId);
                    insertStmt.setInt(3, quantity);
                    insertStmt.setDouble(4, soldPrice);
                    insertStmt.executeUpdate();

                    // Commit the transaction
                    conn.commit();
                } else {
                    // If not enough quantity is available, throw an exception
                    throw new SQLException("Not enough quantity available in the lot.");
                }
            } catch (SQLException e) {
                // Rollback the transaction if an error occurs
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);  // Reset auto-commit
            }
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
        String selectOrderSql = "SELECT lot_id, quantity FROM OnlineOrderDetails WHERE order_id = ?";
        String deleteSql = "DELETE FROM OnlineOrderDetails WHERE order_id = ?";
        String updateLotSql = "UPDATE ProductLot SET quantity = quantity + ? WHERE lot_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start a transaction
            conn.setAutoCommit(false);

            try (PreparedStatement selectStmt = conn.prepareStatement(selectOrderSql); PreparedStatement deleteStmt = conn.prepareStatement(deleteSql); PreparedStatement updateLotStmt = conn.prepareStatement(updateLotSql)) {

                // Get the details of the order to be deleted
                selectStmt.setInt(1, orderId);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    int lotId = rs.getInt("lot_id");
                    int quantity = rs.getInt("quantity");

                    // Delete the order from OnlineOrderDetails
                    deleteStmt.setInt(1, orderId);
                    int rowsAffected = deleteStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        // Restore the quantity in ProductLot
                        updateLotStmt.setInt(1, quantity);  // Add the quantity back
                        updateLotStmt.setInt(2, lotId);
                        updateLotStmt.executeUpdate();
                    }

                    // Commit the transaction
                    conn.commit();
                    return rowsAffected;
                } else {
                    // If the order doesn't exist, no rows will be deleted
                    conn.commit();
                    return 0;
                }
            } catch (SQLException e) {
                // Rollback the transaction if an error occurs
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);  // Reset auto-commit
            }
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
