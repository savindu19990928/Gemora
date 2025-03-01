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

    public void addProductLot(int productId, int colorId, int sizeId, int quantity, int damagedUnit) throws SQLException {
        String sql = "INSERT INTO ProductLot (product_id, color_id, size_id, quantity, damaged_unit) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, colorId);
            stmt.setInt(3, sizeId);
            stmt.setInt(4, quantity);
            stmt.setInt(5, damagedUnit);
            stmt.executeUpdate();
        }
    }

    public void updateProductLot(int lotId, int quantity, int damagedUnit) throws SQLException {
        String sql = "UPDATE ProductLot SET quantity = ?, damaged_unit = ? WHERE lot_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, damagedUnit);
            stmt.setInt(3, lotId);
            stmt.executeUpdate();
        }
    }

    public void deleteProductLot(int lotId) throws SQLException {
        String sql = "DELETE FROM ProductLot WHERE lot_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lotId);
            stmt.executeUpdate();
        }
    }

    public List<JSONObject> getAllProductLots() throws SQLException {
        List<JSONObject> productLots = new ArrayList<>();
        String sql = "SELECT * FROM ProductLot";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("lot_id", rs.getInt("lot_id"));
                json.put("product_id", rs.getInt("product_id"));
                json.put("color_id", rs.getInt("color_id"));
                json.put("size_id", rs.getInt("size_id"));
                json.put("quantity", rs.getInt("quantity"));
                json.put("damaged_unit", rs.getInt("damaged_unit"));
                productLots.add(json);
            }
        }
        return productLots;
    }

    public JSONObject getProductLotById(int lotId) throws SQLException {
        String sql = "SELECT * FROM ProductLot WHERE lot_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lotId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("lot_id", rs.getInt("lot_id"));
                json.put("product_id", rs.getInt("product_id"));
                json.put("color_id", rs.getInt("color_id"));
                json.put("size_id", rs.getInt("size_id"));
                json.put("quantity", rs.getInt("quantity"));
                json.put("damaged_unit", rs.getInt("damaged_unit"));
                return json;
            }
        }
        return null;
    }
}
