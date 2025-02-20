/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

    public void addPayment(int buyerId, double amountReceived, Date paymentDate, String paymentMethod) {
        String sql = "INSERT INTO PaymentFromBuyer (buyer_id, amount_received, payment_date, payment_method) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, buyerId);
            ps.setDouble(2, amountReceived);
            ps.setDate(3, paymentDate);
            ps.setString(4, paymentMethod);
            ps.executeUpdate();
            System.out.println("Payment added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePayment(int paymentId, double amountReceived, String paymentMethod) {
        String sql = "UPDATE PaymentFromBuyer SET amount_received = ?, payment_method = ? WHERE buyer_payment_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, amountReceived);
            ps.setString(2, paymentMethod);
            ps.setInt(3, paymentId);
            ps.executeUpdate();
            System.out.println("Payment updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public List<JSONObject> readPayments() {
        String sql = "SELECT * FROM PaymentFromBuyer";
        List<JSONObject> jsonList = new ArrayList<>(); // List to hold the JSON objects

        try (Connection con = DatabaseConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("buyer_payment_id", rs.getInt("buyer_payment_id"));
                json.put("buyer_id", rs.getInt("buyer_id"));
                json.put("amount_received", rs.getDouble("amount_received"));
                json.put("payment_date", rs.getDate("payment_date").toString()); // Converting date to String
                json.put("payment_method", rs.getString("payment_method"));

                jsonList.add(json); // Add the JSON object to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jsonList; // Return the list of JSON objects
    }
}
