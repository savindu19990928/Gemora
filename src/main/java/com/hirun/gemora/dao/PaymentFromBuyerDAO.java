package com.hirun.gemora.dao;

import java.sql.PreparedStatement;
import com.hirun.gemora.DatabaseConnection;
import java.sql.Date;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class PaymentFromBuyerDAO {

    // Method to add a payment
    public void addPayment(int buyerId, int saleId, double amountReceived, Date paymentDate, String paymentMethod, String description) {
        String sql = "INSERT INTO PaymentFromBuyer (buyer_id, sale_id, amount_received, payment_date, payment_method, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, buyerId);
            ps.setInt(2, saleId);  // Add the sale_id field here
            ps.setDouble(3, amountReceived);
            ps.setDate(4, paymentDate);
            ps.setString(5, paymentMethod);
            ps.setString(6, description);  // Add the description field here
            ps.executeUpdate();
            System.out.println("Payment added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update a payment
    public void updatePayment(int paymentId, double amountReceived, String paymentMethod, String description) {
        String sql = "UPDATE PaymentFromBuyer SET amount_received = ?, payment_method = ?, description = ? WHERE buyer_payment_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, amountReceived);
            ps.setString(2, paymentMethod);
            ps.setString(3, description);  // Update the description field
            ps.setInt(4, paymentId);
            ps.executeUpdate();
            System.out.println("Payment updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to remove a payment
    public void removePayment(int paymentId) {
        String sql = "DELETE FROM PaymentFromBuyer WHERE buyer_payment_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.executeUpdate();
            System.out.println("Payment removed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to read all payments
    public List<JSONObject> readPayments() {
        String sql = "SELECT * FROM PaymentFromBuyer";
        List<JSONObject> jsonList = new ArrayList<>(); // List to hold the JSON objects

        try (Connection con = DatabaseConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("buyer_payment_id", rs.getInt("buyer_payment_id"));
                json.put("buyer_id", rs.getInt("buyer_id"));
                json.put("sale_id", rs.getInt("sale_id"));  // Added the sale_id field here
                json.put("amount_received", rs.getDouble("amount_received"));
                json.put("payment_date", rs.getDate("payment_date").toString()); // Converting date to String
                json.put("payment_method", rs.getString("payment_method"));
                json.put("description", rs.getString("description"));  // Added description field

                jsonList.add(json); // Add the JSON object to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jsonList; // Return the list of JSON objects
    }
}
