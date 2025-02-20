package com.hirun.gemora.dao;

import com.hirun.gemora.DatabaseConnection;
import org.json.JSONObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentToSupplierDAO {

    public void addPayment(int supplierId, int materialPurchaseId, double amountPaid, String paymentMethod) {
        String sql = "INSERT INTO PaymentToSupplier (supplier_id, material_purchase_id, amount_paid, payment_method) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            ps.setInt(2, materialPurchaseId);
            ps.setDouble(3, amountPaid);
            ps.setString(4, paymentMethod);
            ps.executeUpdate();
            System.out.println("Payment added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePayment(int paymentId, double amountPaid, String paymentMethod) {
        String sql = "UPDATE PaymentToSupplier SET amount_paid = ?, payment_method = ? WHERE supplier_payment_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, amountPaid);
            ps.setString(2, paymentMethod);
            ps.setInt(3, paymentId);
            ps.executeUpdate();
            System.out.println("Payment updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePayment(int paymentId) {
        String sql = "DELETE FROM PaymentToSupplier WHERE supplier_payment_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.executeUpdate();
            System.out.println("Payment removed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<JSONObject> readPayments() {
        String sql = "SELECT * FROM PaymentToSupplier";
        List<JSONObject> jsonList = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("supplier_payment_id", rs.getInt("supplier_payment_id"));
                json.put("supplier_id", rs.getInt("supplier_id"));
                json.put("material_purchase_id", rs.getInt("material_purchase_id"));
                json.put("amount_paid", rs.getDouble("amount_paid"));
                json.put("payment_date", rs.getDate("payment_date").toString());
                json.put("payment_method", rs.getString("payment_method"));
                jsonList.add(json);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonList;
    }
}