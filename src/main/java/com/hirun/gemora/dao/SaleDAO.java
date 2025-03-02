package com.hirun.gemora.dao;

import com.hirun.gemora.DatabaseConnection;
import org.json.JSONObject;
import java.sql.*;

public class SaleDAO {

    // 1. Make Sale (Insert Sale Record)
    public static int makeSale(int buyerId, String saleName) {
        String saleQuery = "INSERT INTO LotSale (buyer_id, name) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Insert the sale record
            try (PreparedStatement saleStmt = conn.prepareStatement(saleQuery, Statement.RETURN_GENERATED_KEYS)) {
                saleStmt.setInt(1, buyerId);
                saleStmt.setString(2, saleName);
                saleStmt.executeUpdate();

                // Get generated sale ID
                try (ResultSet rs = saleStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int saleId = rs.getInt(1);
                        conn.commit();
                        return saleId;
                    } else {
                        conn.rollback();
                        return -1; // Indicate failure
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Indicate failure
        }
    }
    
    // 2. Delete Sale (Delete Sale Record)
    public static boolean deleteSale(int saleId) {
        String deleteSaleQuery = "DELETE FROM LotSale WHERE sale_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Delete Sale Record
            try (PreparedStatement deleteSaleStmt = conn.prepareStatement(deleteSaleQuery)) {
                deleteSaleStmt.setInt(1, saleId);
                deleteSaleStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Update Sale Record (Update Sale Information)
    public static boolean updateSale(int saleId, String saleName) {
        String updateSaleQuery = "UPDATE LotSale SET name = ? WHERE sale_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Update Sale Record
            try (PreparedStatement updateSaleStmt = conn.prepareStatement(updateSaleQuery)) {
                updateSaleStmt.setString(1, saleName);
                updateSaleStmt.setInt(2, saleId);
                updateSaleStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Read Sale by ID (Retrieve Sale Information)
    public static JSONObject getSaleById(int saleId) {
        String query = "SELECT s.sale_id, s.buyer_id, s.name AS sale_name, s.sale_date "
                + "FROM LotSale s WHERE s.sale_id = ?";
        JSONObject sale = new JSONObject();

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, saleId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                sale.put("sale_id", rs.getInt("sale_id"));
                sale.put("buyer_id", rs.getInt("buyer_id"));
                sale.put("sale_name", rs.getString("sale_name"));
                sale.put("sale_date", rs.getTimestamp("sale_date"));
            } else {
                return null; // No sale found
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return sale;
    }

    public static double getTotalPaymentForLot(int sale_id) {
        String query = "SELECT SUM(quantity_sold * item_Price) AS total_payment FROM LotSaleDetails WHERE sale_id = ? GROUP BY lot_id";
        double totalPayment = 0.0;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, sale_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                totalPayment = rs.getDouble("total_payment");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalPayment;
    }
}
