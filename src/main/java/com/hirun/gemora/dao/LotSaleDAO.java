/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hirun.gemora.dao;

import java.sql.PreparedStatement;
import com.hirun.gemora.DatabaseConnection;
import java.sql.Date;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class LotSaleDAO {

    public void addLotSale(int buyerId, int lotId, double quantitySold, double pricePerUnit, double totalPrice, Date saleDate) {
        String sql = "INSERT INTO LotSale (buyer_id, lot_id, quantity_sold, price_per_unit, total_price, sale_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, buyerId);
            ps.setInt(2, lotId);
            ps.setDouble(3, quantitySold);
            ps.setDouble(4, pricePerUnit);
            ps.setDouble(5, totalPrice);
            ps.setDate(6, saleDate);
            ps.executeUpdate();
            System.out.println("Lot sale added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLotSale(int saleId, double quantitySold, double pricePerUnit, double totalPrice) {
        String sql = "UPDATE LotSale SET quantity_sold = ?, price_per_unit = ?, total_price = ? WHERE sale_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, quantitySold);
            ps.setDouble(2, pricePerUnit);
            ps.setDouble(3, totalPrice);
            ps.setInt(4, saleId);
            ps.executeUpdate();
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
                json.put("lot_id", rs.getInt("lot_id"));
                json.put("quantity_sold", rs.getDouble("quantity_sold"));
                json.put("price_per_unit", rs.getDouble("price_per_unit"));
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
