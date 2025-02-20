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

public class BuyerDAO {

    public static void addBuyer(String name, String address, String ownerName, String contactInfo) {
        String sql = "INSERT INTO Buyer (name, address, ownerName, contact_info, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, ownerName);
            ps.setString(4, contactInfo);
            ps.setBoolean(5, true);
            ps.executeUpdate();
            System.out.println("Buyer added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateBuyer(String name, String address, String ownerName, String contactInfo) {
        String sql = "UPDATE Buyer SET name = ?, address = ?, ownerName = ?, contact_info = ? WHERE buyer_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, ownerName);
            ps.setString(4, contactInfo);
            ps.setString(5, name);
            ps.executeUpdate();
            System.out.println("Buyer updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeBuyer(String name) {
        String sql = "DELETE FROM Buyer WHERE buyer_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
            System.out.println("Buyer removed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<JSONObject> readBuyers() {
        String sql = "SELECT * FROM Buyer";
        List<JSONObject> jsonList = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("buyer_id", rs.getInt("buyer_id"));
                json.put("name", rs.getString("name"));
                json.put("address", rs.getString("address"));
                json.put("ownerName", rs.getString("ownerName"));
                json.put("contact_info", rs.getString("contact_info"));
                json.put("is_active", rs.getBoolean("is_active"));
                json.put("created_at", rs.getTimestamp("created_at"));
                jsonList.add(json);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonList;
    }

    public static JSONObject readBuyerByName(String buyerName) {
        String sql = "SELECT * FROM Buyer WHERE name LIKE ? LIMIT 1";
        JSONObject json = null;

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, "%" + buyerName + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    json = new JSONObject();
                    json.put("buyer_id", rs.getInt("buyer_id"));
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

    public static JSONObject readBuyerById(int id) {
        String sql = "SELECT * FROM Buyer WHERE buyer_id LIKE ? LIMIT 1";
        JSONObject json = null;

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    json = new JSONObject();
                    json.put("buyer_id", rs.getInt("buyer_id"));
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
