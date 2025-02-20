package com.hirun.gemora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.hirun.gemora.DatabaseConnection;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class MaterialAdditionalCostDAO {

    public void addMaterialAdditionalCost(int materialId, String title, String costDescription, double costAmount) {
        String sql = "INSERT INTO MaterialAdditionalCost (material_id, title, cost_description, cost_amount) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            ps.setString(2, title);
            ps.setString(3, costDescription);
            ps.setDouble(4, costAmount);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMaterialAdditionalCost(int costId, String title, String costDescription, double costAmount) {
        String sql = "UPDATE MaterialAdditionalCost SET title = ?, cost_description = ?, cost_amount = ? WHERE cost_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, costDescription);
            ps.setDouble(3, costAmount);
            ps.setInt(4, costId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeMaterialAdditionalCost(int costId) {
        String sql = "DELETE FROM MaterialAdditionalCost WHERE cost_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, costId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<JSONObject> readMaterialAdditionalCostsByMaterialId(int materialId) {
        String sql = "SELECT * FROM MaterialAdditionalCost WHERE material_id = ?";
        List<JSONObject> jsonList = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("cost_id", rs.getInt("cost_id"));
                    json.put("material_id", rs.getInt("material_id"));
                    json.put("title", rs.getString("title"));
                    json.put("cost_description", rs.getString("cost_description"));
                    json.put("cost_amount", rs.getDouble("cost_amount"));
                    jsonList.add(json);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonList;
    }

    public static JSONObject readMaterialAdditionalCostById(int costId) {
        String sql = "SELECT * FROM MaterialAdditionalCost WHERE cost_id = ?";
        JSONObject json = null;

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, costId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    json = new JSONObject();
                    json.put("cost_id", rs.getInt("cost_id"));
                    json.put("material_id", rs.getInt("material_id"));
                    json.put("title", rs.getString("title"));
                    json.put("cost_description", rs.getString("cost_description"));
                    json.put("cost_amount", rs.getDouble("cost_amount"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return json;
    }
}
