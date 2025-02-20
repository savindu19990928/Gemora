package com.hirun.gemora.dao;

import java.sql.PreparedStatement;
import com.hirun.gemora.DatabaseConnection;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LotSaleDAO {

    public void addLotSale(int buyerId, String name, List<JSONObject> lotSaleDetails) {
        String sqlLotSale = "INSERT INTO LotSale (buyer_id, name) VALUES (?, ?)";
        try (Connection con = DatabaseConnection.getConnection()) {
            // Insert the sale record into LotSale
            PreparedStatement psLotSale = con.prepareStatement(sqlLotSale, Statement.RETURN_GENERATED_KEYS);
            psLotSale.setInt(1, buyerId);
            psLotSale.setString(2, name);
            psLotSale.executeUpdate();

            // Get the generated sale_id from the LotSale table
            ResultSet rs = psLotSale.getGeneratedKeys();
            int saleId = 0;
            if (rs.next()) {
                saleId = rs.getInt(1);
            }

            // Insert records into LotSaleDetails using JSON objects
            String sqlLotSaleDetails = "INSERT INTO LotSaleDetails (sale_id, lot_id, quantity_sold) VALUES (?, ?, ?)";
            PreparedStatement psLotSaleDetails = con.prepareStatement(sqlLotSaleDetails);

            for (JSONObject detail : lotSaleDetails) {
                psLotSaleDetails.setInt(1, saleId);
                psLotSaleDetails.setInt(2, detail.getInt("lot_id"));
                psLotSaleDetails.setDouble(3, detail.getDouble("quantity_sold"));
                psLotSaleDetails.addBatch();
            }

            psLotSaleDetails.executeBatch(); // Execute all the batch inserts
            System.out.println("Lot sale and details added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLotSale(int saleId, double totalPrice, List<JSONObject> lotSaleDetails) {
        String sqlLotSale = "UPDATE LotSale SET total_price = ? WHERE sale_id = ?";
        try (Connection con = DatabaseConnection.getConnection()) {
            // Update the LotSale record
            PreparedStatement psLotSale = con.prepareStatement(sqlLotSale);
            psLotSale.setDouble(1, totalPrice);
            psLotSale.setInt(2, saleId);
            psLotSale.executeUpdate();

            // Remove existing details and insert updated ones
            String deleteLotSaleDetails = "DELETE FROM LotSaleDetails WHERE sale_id = ?";
            PreparedStatement psDelete = con.prepareStatement(deleteLotSaleDetails);
            psDelete.setInt(1, saleId);
            psDelete.executeUpdate();

            // Insert new details using JSON objects
            String sqlLotSaleDetails = "INSERT INTO LotSaleDetails (sale_id, lot_id, quantity_sold) VALUES (?, ?, ?)";
            PreparedStatement psLotSaleDetails = con.prepareStatement(sqlLotSaleDetails);

            for (JSONObject detail : lotSaleDetails) {
                psLotSaleDetails.setInt(1, saleId);
                psLotSaleDetails.setInt(2, detail.getInt("lot_id"));
                psLotSaleDetails.setDouble(3, detail.getDouble("quantity_sold"));
                psLotSaleDetails.addBatch();
            }

            psLotSaleDetails.executeBatch(); // Execute the batch insert
            System.out.println("Lot sale updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeLotSale(int saleId) {
        String sql = "DELETE FROM LotSale WHERE sale_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            ps.executeUpdate();
            System.out.println("Lot sale removed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<JSONObject> readLotSales() {
        String sql = "SELECT * FROM LotSale";
        List<JSONObject> jsonList = new ArrayList<>(); // List to hold the JSON objects

        try (Connection con = DatabaseConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("sale_id", rs.getInt("sale_id"));
                json.put("buyer_id", rs.getInt("buyer_id"));
                json.put("name", rs.getString("name"));
                json.put("total_price", rs.getDouble("total_price"));
                json.put("sale_date", rs.getDate("sale_date").toString()); // Converting date to string

                jsonList.add(json); // Add the JSON object to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jsonList; // Return the list of JSON objects
    }
}
