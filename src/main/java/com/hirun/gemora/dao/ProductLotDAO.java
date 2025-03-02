package com.hirun.gemora.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import com.hirun.gemora.DatabaseConnection;
import java.math.BigDecimal;
import javax.swing.table.DefaultTableModel;

public class ProductLotDAO {

    public static void addProductLot(int productId, int colorId, int sizeId, int quantity, int damagedUnit) throws SQLException {
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

    public static void updateProductLot(int lotId, int quantity, int damagedUnit) throws SQLException {
        String sql = "UPDATE ProductLot SET quantity = ?, damaged_unit = ? WHERE lot_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, damagedUnit);
            stmt.setInt(3, lotId);
            stmt.executeUpdate();
        }
    }

    public static void deleteProductLot(int lotId) throws SQLException {
        String sql = "DELETE FROM ProductLot WHERE lot_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lotId);
            stmt.executeUpdate();
        }
    }

    public static List<JSONObject> getAllProductLots() throws SQLException {
        List<JSONObject> productLots = new ArrayList<>();
        String sql = "SELECT ProductLot.lot_id, ProductLot.product_id, ProductLot.color_id, ProductLot.size_id, ProductLot.quantity, ProductLot.damaged_unit, Product.name "
                + "FROM ProductLot "
                + "JOIN Product ON ProductLot.product_id = Product.product_id"; // Join with Product to get the name

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("lot_id", rs.getInt("lot_id"));
                json.put("product_id", rs.getInt("product_id"));
                json.put("color_id", rs.getInt("color_id"));
                json.put("size_id", rs.getInt("size_id"));
                json.put("quantity", rs.getInt("quantity"));
                json.put("damaged_unit", rs.getInt("damaged_unit"));
                json.put("name", rs.getString("name")); // Add product name
                productLots.add(json);
            }
        }
        return productLots;
    }

    public static JSONObject getProductLotById(int lotId) throws SQLException {
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

    public static JSONObject getProductLotByDetails(String productName, String colorName, String sizeName) throws SQLException {
        String sql = "SELECT pl.lot_id AS lot_id, p.name AS product_name, c.name AS color_name, s.name AS size_name, "
                + "pl.quantity, pl.damaged_unit "
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
                return json;
            }
        }
        return null;
    }

    public static int getLotIdByDetails(String productName, String colorName, String sizeName) {
        int lotId = -1;
        String sql = "SELECT pl.lot_id "
                + "FROM ProductLot pl "
                + "JOIN Product p ON pl.product_id = p.product_id "
                + "JOIN Color c ON pl.color_id = c.color_id "
                + "JOIN Size s ON pl.size_id = s.size_id "
                + "WHERE p.name = ? AND c.name = ? AND s.name = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productName);
            stmt.setString(2, colorName);
            stmt.setString(3, sizeName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    lotId = rs.getInt("lot_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lotId;
    }

    public static void loadProductLotsIntoTable(DefaultTableModel tableModel, int saleId) throws SQLException {
        String sql = "SELECT pl.lot_id, pl.product_id, p.name AS product_name, SUM(lsd.quantity_sold) AS quantity, "
                + "SUM(lsd.quantity_sold * lsd.item_price) AS total_price "
                + "FROM ProductLot pl "
                + "JOIN Product p ON pl.product_id = p.product_id "
                + "JOIN LotSaleDetails lsd ON pl.lot_id = lsd.lot_id "
                + "WHERE lsd.sale_id = ? "
                + "GROUP BY pl.lot_id, pl.product_id, p.name"; // Group by lot_id, product_id, and product name

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set the sale_id in the query
            pstmt.setInt(1, saleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                // Clear existing rows
                tableModel.setRowCount(0);

                // Iterate through the result set
                while (rs.next()) {
                    int lotId = rs.getInt("lot_id");
                    String productName = rs.getString("product_name");
                    int quantity = rs.getInt("quantity");
                    BigDecimal totalPrice = rs.getBigDecimal("total_price");

                    // Add a row to the table model
                    tableModel.addRow(new Object[]{lotId, productName, quantity, totalPrice});
                }
            }
        }
    }

    public static void loadProductsOnlineIntoTable(DefaultTableModel tableModel, int customerId) throws SQLException {
        String sql = "SELECT ood.order_id, pl.lot_id, p.name AS product_name, ood.quantity, "
                + "(ood.sold_price * ood.quantity) AS total_price "
                + "FROM OnlineOrderDetails ood "
                + "JOIN ProductLot pl ON ood.lot_id = pl.lot_id "
                + "JOIN Product p ON pl.product_id = p.product_id "
                + "WHERE ood.customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the customer_id in the query
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                // Clear existing rows
                tableModel.setRowCount(0);

                // Iterate through the result set
                while (rs.next()) {
                    int lotId = rs.getInt("order_id");
                    String productName = rs.getString("product_name");
                    int quantity = rs.getInt("quantity");
                    BigDecimal totalPrice = rs.getBigDecimal("total_price");

                    // Add a row to the table model
                    tableModel.addRow(new Object[]{lotId, productName, quantity, totalPrice});
                }
            }
        }
    }
}
