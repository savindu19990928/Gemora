package com.hirun.gemora.dao;

import java.sql.PreparedStatement;
import com.hirun.gemora.DatabaseConnection;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MaterialDAO {

    public static void addMaterial(String name, int unit, double unitPrice, int dmg) {
        String sql = "INSERT INTO Material (name, unit, unit_price) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, unit);
            ps.setDouble(3, unitPrice);
            ps.setInt(4, dmg);
            ps.executeUpdate();
            System.out.println("Material added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateMaterial(int materialId, String name, int unit, int dmg, double unitPrice) {
        String sql = "UPDATE Material SET name = ?, unit = ?, unit_price = ?, damaged_unit = ? WHERE material_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, unit);
            ps.setDouble(3, unitPrice);
            ps.setInt(4, dmg);
            ps.setInt(5, materialId);
            ps.executeUpdate();
            System.out.println("Material updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeMaterial(int materialId) {
        String sql = "DELETE FROM Material WHERE material_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            ps.executeUpdate();
            System.out.println("Material removed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject readMaterial() {
        String sql = "SELECT * FROM Material LIMIT 1"; // Ensure only one record is fetched
        JSONObject json = new JSONObject();

        try (Connection con = DatabaseConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) { // Fetch only one row
                json.put("material_id", rs.getInt("material_id"));
                json.put("name", rs.getString("name"));
                json.put("unit", rs.getInt("unit"));
                json.put("unit_price", rs.getDouble("unit_price"));
                json.put("damaged_unit", rs.getInt("damaged_unit"));
                json.put("created_at", rs.getTimestamp("created_at"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return json; // Return the JSON object
    }

}
