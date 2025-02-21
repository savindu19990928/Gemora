package com.hirun.gemora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.hirun.gemora.DatabaseConnection;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class SupplierDAO {

    public static void addSupplier(String name, String address, String ownerName, String contactInfo) {
        String sql = "INSERT INTO Supplier (name, address, ownerName, contact_info, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, ownerName);
            ps.setString(4, contactInfo);
            ps.setBoolean(5, true);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateSupplier(String name, String address, String ownerName, String contactInfo) {
        String sql = "UPDATE Supplier SET name = ?, address = ?, ownerName = ?, contact_info = ? WHERE name = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, ownerName);
            ps.setString(4, contactInfo);
            ps.setString(5, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeSupplier(String name) {
        String sql = "DELETE FROM Supplier WHERE name = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<JSONObject> readSuppliers() {
        String sql = "SELECT * FROM Supplier";
        List<JSONObject> jsonList = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql); // Use PreparedStatement here
                 ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("supplier_id", rs.getInt("supplier_id"));
                json.put("name", rs.getString("name"));
                json.put("address", rs.getString("address"));
                json.put("ownerName", rs.getString("ownerName"));
                json.put("contact_info", rs.getString("contact_info"));
                json.put("is_active", rs.getBoolean("is_active"));
                json.put("created_at", rs.getTimestamp("created_at"));
                jsonList.add(json);
            }
            System.out.println("Fetched suppliers: " + jsonList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonList;
    }

    public static JSONObject readSupplierByName(String supplierName) {
        String sql = "SELECT * FROM Supplier WHERE name LIKE ? LIMIT 1";
        JSONObject json = null;

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, "%" + supplierName + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    json = new JSONObject();
                    json.put("supplier_id", rs.getInt("supplier_id"));
                    json.put("name", rs.getString("name"));
                    json.put("address", rs.getString("address"));
                    json.put("ownerName", rs.getString("ownerName"));
                    json.put("contact_info", rs.getString("contact_info"));
                    json.put("is_active", rs.getBoolean("is_active"));
                    json.put("created_at", rs.getTimestamp("created_at"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return json;
    }
}
