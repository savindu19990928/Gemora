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

public class ProductLotDAO {

    public static int addProductLot(String name, int quantity, int damagedUnit) {
        String sql = "INSERT INTO ProductLot (name, quantity, damaged_unit) VALUES (?, ?, ?)";
        int generatedId = -1;
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setInt(3, damagedUnit);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    public static boolean updateProductLot(int lotId, String name, int quantity, int damagedUnit) {
        String sql = "UPDATE ProductLot SET name = ?, quantity = ?, damaged_unit = ? WHERE lot_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setInt(3, damagedUnit);
            ps.setInt(4, lotId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static void removeProductLot(int lotId) {
        String sql = "DELETE FROM ProductLot WHERE lot_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lotId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<JSONObject> readProductLots() {
        String sql = "SELECT * FROM ProductLot";
        List<JSONObject> productLots = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("lot_id", rs.getInt("lot_id"));
                json.put("product_id", rs.getInt("product_id"));
                json.put("name", rs.getString("name"));
                json.put("quantity", rs.getInt("quantity"));
                json.put("damaged_unit", rs.getInt("damaged_unit"));
                productLots.add(json);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productLots;
    }

    public static JSONObject readProductLotById(int lotId) {
        String sql = "SELECT * FROM ProductLot WHERE lot_id = ?";
        JSONObject json = new JSONObject();
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lotId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                json.put("lot_id", rs.getInt("lot_id"));
                json.put("name", rs.getString("name"));
                json.put("quantity", rs.getInt("quantity"));
                json.put("damaged_unit", rs.getInt("damaged_unit"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return json;
    }
}
