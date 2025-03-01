package com.hirun.gemora.dao;

import org.json.JSONObject;
import com.hirun.gemora.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OnlineCustomerDAO {

    // Add a new online customer
    public static void addOnlineCustomer(String customerName, String phoneNumber, String address,
            Timestamp orderedDate, Timestamp deliveredDate, String issue) throws SQLException {
        String sql = "INSERT INTO OnlineCustomer (customer_name, phone_number, address, ordered_date, delivered_date, issue) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerName);
            stmt.setString(2, phoneNumber);
            stmt.setString(3, address);
            stmt.setTimestamp(4, orderedDate);
            stmt.setTimestamp(5, deliveredDate);
            stmt.setString(6, issue);
            stmt.executeUpdate();
        }
    }

    // Update an existing online customer
    public static void updateOnlineCustomer(int customerId, String customerName, String phoneNumber, String address,
            Timestamp orderedDate, Timestamp deliveredDate, String issue) throws SQLException {
        String sql = "UPDATE OnlineCustomer SET customer_name = ?, phone_number = ?, address = ?, ordered_date = ?, delivered_date = ?, issue = ? WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerName);
            stmt.setString(2, phoneNumber);
            stmt.setString(3, address);
            stmt.setTimestamp(4, orderedDate);
            stmt.setTimestamp(5, deliveredDate);
            stmt.setString(6, issue);
            stmt.setInt(7, customerId);
            stmt.executeUpdate();
        }
    }

    // Delete an online customer by customer_id
    public static void deleteOnlineCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM OnlineCustomer WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.executeUpdate();
        }
    }

    // Get all online customers
    public static List<JSONObject> getAllOnlineCustomers() throws SQLException {
        List<JSONObject> customers = new ArrayList<>();
        String sql = "SELECT customer_id, customer_name, phone_number, address, ordered_date, delivered_date, issue FROM OnlineCustomer";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("customer_id", rs.getInt("customer_id"));
                    json.put("customer_name", rs.getString("customer_name"));
                    json.put("phone_number", rs.getString("phone_number"));
                    json.put("address", rs.getString("address"));
                    json.put("ordered_date", rs.getTimestamp("ordered_date"));
                    json.put("delivered_date", rs.getTimestamp("delivered_date"));
                    json.put("issue", rs.getString("issue"));
                    customers.add(json);
                }
            }
        }
        return customers;
    }

    // Get an online customer by customer_id
    public static JSONObject getOnlineCustomerById(int customerId) throws SQLException {
        String sql = "SELECT customer_id, customer_name, phone_number, address, ordered_date, delivered_date, issue FROM OnlineCustomer WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("customer_id", rs.getInt("customer_id"));
                    json.put("customer_name", rs.getString("customer_name"));
                    json.put("phone_number", rs.getString("phone_number"));
                    json.put("address", rs.getString("address"));
                    json.put("ordered_date", rs.getTimestamp("ordered_date"));
                    json.put("delivered_date", rs.getTimestamp("delivered_date"));
                    json.put("issue", rs.getString("issue"));
                    return json;
                }
            }
        }
        return null;
    }
}
