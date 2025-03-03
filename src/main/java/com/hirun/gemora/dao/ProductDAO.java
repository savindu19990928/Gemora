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
        String selectSql = "SELECT pl.lot_id FROM ProductLot pl "
                + "JOIN Product p ON pl.product_id = p.product_id "
                + "JOIN Color c ON pl.color_id = c.color_id "
                + "JOIN Size s ON pl.size_id = s.size_id "
                + "WHERE p.name = ? AND c.name = ? AND s.name = ?";

        String updateSql = "UPDATE ProductLot pl "
                + "JOIN Product p ON pl.product_id = p.product_id "
                + "JOIN Color c ON pl.color_id = c.color_id "
                + "JOIN Size s ON pl.size_id = s.size_id "
                + "SET pl.quantity = ?, pl.damaged_unit = ? "
                + "WHERE p.name = ? AND c.name = ? AND s.name = ?";

        String insertSql = "INSERT INTO ProductLot (product_id, color_id, size_id, quantity, damaged_unit) "
                + "VALUES ((SELECT product_id FROM Product WHERE name = ?), "
                + "(SELECT color_id FROM Color WHERE name = ?), "
                + "(SELECT size_id FROM Size WHERE name = ?), ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

            selectStmt.setString(1, productName);
            selectStmt.setString(2, colorName);
            selectStmt.setString(3, sizeName);

            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    // Update existing record
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, quantity);
                        updateStmt.setInt(2, damagedUnit);
                        updateStmt.setString(3, productName);
                        updateStmt.setString(4, colorName);
                        updateStmt.setString(5, sizeName);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Insert new record
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, productName);
                        insertStmt.setString(2, colorName);
                        insertStmt.setString(3, sizeName);
                        insertStmt.setInt(4, quantity);
                        insertStmt.setInt(5, damagedUnit);
                        insertStmt.executeUpdate();
                    }
                }
            }
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

    public static int getTotalProductsByProductName(String productName) throws SQLException {
        // First, retrieve the product_id by product name
        String getProductIdSql = "SELECT p.product_id FROM Product p WHERE p.name = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(getProductIdSql)) {

            stmt.setString(1, productName);  // Set the product name

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int productId = rs.getInt("product_id");  // Get the product_id

                // Now, sum the quantities from the ProductLot table for this product_id
                String getTotalQuantitySql = "SELECT SUM(pl.quantity) AS total_quantity "
                        + "FROM ProductLot pl "
                        + "WHERE pl.product_id = ?";

                try (PreparedStatement totalStmt = conn.prepareStatement(getTotalQuantitySql)) {
                    totalStmt.setInt(1, productId);  // Set the product_id

                    ResultSet totalRs = totalStmt.executeQuery();
                    if (totalRs.next()) {
                        return totalRs.getInt("total_quantity");  // Return the total quantity
                    }
                }
            }
        }
        return 0;  // Return 0 if no matching product or product lots found
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
