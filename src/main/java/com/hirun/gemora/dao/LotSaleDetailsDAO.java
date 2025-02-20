package com.hirun.gemora.dao;

import com.hirun.gemora.DatabaseConnection;
import org.json.JSONObject;
import org.json.JSONArray;
import java.sql.*;

public class LotSaleDetailsDAO {

    // Add a new lot sale detail
    public static boolean addLotSaleDetail(int saleId, int lotId, int quantitySold, double itemPrice) {
        // SQL query to check if saleId exists in the LotSales table
        String checkSaleExistsSql = "SELECT sale_id FROM LotSale WHERE sale_id = ?";

        // SQL query to insert into LotSaleDetails table
        String insertSql = "INSERT INTO LotSaleDetails (sale_id, lot_id, quantity_sold, item_Price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement checkStmt = conn.prepareStatement(checkSaleExistsSql)) {

            // Check if saleId exists
            checkStmt.setInt(1, saleId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                // saleId does not exist in LotSales, return false or handle the error
                System.out.println("Error: sale_id " + saleId + " does not exist in LotSales.");
                return false;
            }

            // saleId exists, proceed with the insert
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, saleId);
                insertStmt.setInt(2, lotId);
                insertStmt.setInt(3, quantitySold);
                insertStmt.setDouble(4, itemPrice);

                return insertStmt.executeUpdate() > 0; // Returns true if insert is successful
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update an existing lot sale detail
    public static boolean updateLotSaleDetail(int lotSaleId, int saleId, int lotId, int quantitySold, double itemPrice) {
        String sql = "UPDATE LotSaleDetails SET sale_id = ?, lot_id = ?, quantity_sold = ?, item_Price = ? WHERE lot_sale_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            stmt.setInt(2, lotId);
            stmt.setInt(3, quantitySold);
            stmt.setDouble(4, itemPrice);
            stmt.setInt(5, lotSaleId);

            return stmt.executeUpdate() > 0; // Returns true if update successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a sale detail by lotSaleId
    public static boolean deleteLotSaleDetail(int lotSaleId) {
        String sql = "DELETE FROM LotSaleDetails WHERE lot_sale_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lotSaleId);
            return stmt.executeUpdate() > 0; // Returns true if deletion successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all lot sale details as JSON Array
    public static JSONArray getAllLotSaleDetails() {
        JSONArray saleDetails = new JSONArray();
        String sql = "SELECT * FROM LotSaleDetails";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("lot_sale_id", rs.getInt("lot_sale_id"));
                json.put("sale_id", rs.getInt("sale_id"));
                json.put("lot_id", rs.getInt("lot_id"));
                json.put("quantity_sold", rs.getInt("quantity_sold"));
                json.put("item_Price", rs.getDouble("item_Price"));
                saleDetails.put(json);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return saleDetails;
    }

    // Get lot sale details by sale ID as JSON Array
    public static JSONArray getLotSaleDetailsBySaleId(int saleId) {
        JSONArray saleDetails = new JSONArray();
        String sql = "SELECT * FROM LotSaleDetails WHERE sale_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("lot_sale_id", rs.getInt("lot_sale_id"));
                    json.put("sale_id", rs.getInt("sale_id"));
                    json.put("lot_id", rs.getInt("lot_id"));
                    json.put("quantity_sold", rs.getInt("quantity_sold"));
                    json.put("item_Price", rs.getDouble("item_Price"));
                    saleDetails.put(json);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return saleDetails;
    }

    // Get a single lot sale detail by ID as JSON Object
    public static JSONObject getLotSaleDetailById(int lotSaleId) {
        JSONObject json = new JSONObject();
        String sql = "SELECT * FROM LotSaleDetails WHERE lot_sale_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lotSaleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    json.put("lot_sale_id", rs.getInt("lot_sale_id"));
                    json.put("sale_id", rs.getInt("sale_id"));
                    json.put("lot_id", rs.getInt("lot_id"));
                    json.put("quantity_sold", rs.getInt("quantity_sold"));
                    json.put("item_Price", rs.getDouble("item_Price"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return json;
    }
}
