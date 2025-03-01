package com.hirun.gemora.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import com.hirun.gemora.DatabaseConnection;

public class ProductDAO {

    // Add a new product
    public static void addProduct(String name) throws SQLException {
        String sql = "INSERT INTO Product (name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }

    // Update product lot (quantity and damaged unit) based on product, color, and size
    public static boolean updateProduct(String productName, String colorName, String sizeName,
            int quantity, int damagedUnit) throws SQLException {
        String sql = "UPDATE ProductLot pl "
                + "JOIN Product p ON pl.product_id = p.product_id "
                + "JOIN Color c ON pl.color_id = c.color_id "
                + "JOIN Size s ON pl.size_id = s.size_id "
                + "SET pl.quantity = ?, pl.damaged_unit = ? "
                + "WHERE p.name = ? AND c.name = ? AND s.name = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, damagedUnit);
            stmt.setString(3, productName);
            stmt.setString(4, colorName);
            stmt.setString(5, sizeName);
            stmt.executeUpdate();
        }
        return true;
    }

    // Delete product by name
    public static void deleteProduct(String name) throws SQLException {
        String sql = "DELETE FROM Product WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }

    // Get all products
    public static List<JSONObject> getAllProducts() throws SQLException {
        List<JSONObject> products = new ArrayList<>();
        String sql = "SELECT product_id, name FROM Product";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("product_id", rs.getInt("product_id"));
                json.put("name", rs.getString("name"));
                products.add(json);
            }
        }
        return products;
    }

    // Get product by ID
    public static JSONObject getProductById(int productId) throws SQLException {
        String sql = "SELECT product_id, name FROM Product WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("product_id", rs.getInt("product_id"));
                json.put("name", rs.getString("name"));
                return json;
            }
        }
        return null;
    }

    // Get product by name
    public static JSONObject getProductByName(String name) throws SQLException {
        String sql = "SELECT product_id, name FROM Product WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("product_id", rs.getInt("product_id"));
                json.put("name", rs.getString("name"));
                return json;
            }
        }
        return null;
    }

    // Get product lot details by product name, color, and size
    public static JSONObject getProductLotByDetails(String productName, String colorName, String sizeName) throws SQLException {
        String sql = "SELECT pl.lot_id, p.name AS product_name, c.name AS color_name, s.name AS size_name, pl.quantity, pl.damaged_unit, pl.production_date "
                + "FROM ProductLot pl "
                + "JOIN Product p ON pl.product_id = p.product_id "
                + "JOIN Color c ON pl.color_id = c.color_id "
                + "JOIN Size s ON pl.size_id = s.size_id "
                + "WHERE p.name = ? AND c.name = ? AND s.name = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productName);
            stmt.setString(2, colorName);
            stmt.setString(3, sizeName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("lot_id", rs.getInt("lot_id"));
                json.put("product_name", rs.getString("product_name"));
                json.put("color_name", rs.getString("color_name"));
                json.put("size_name", rs.getString("size_name"));
                json.put("quantity", rs.getInt("quantity"));
                json.put("damaged_unit", rs.getInt("damaged_unit"));
                json.put("production_date", rs.getTimestamp("production_date"));
                return json;
            }
        }
        return null;
    }

    public static String[] getAllProductNames() throws SQLException {
        String sql = "SELECT name FROM Product";
        List<String> productNames = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                productNames.add(rs.getString("name"));
            }
        }

        // Convert list to array
        return productNames.toArray(String[]::new);
    }
}
