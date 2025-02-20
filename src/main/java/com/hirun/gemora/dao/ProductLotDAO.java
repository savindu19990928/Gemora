/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hirun.gemora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.hirun.gemora.DatabaseConnection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

class ProductLotDAO {

    public void addProductLot(int productId, String lotNumber, double quantity, Date productionDate, double totalCost) {
        String sql = "INSERT INTO ProductLot (product_id, lot_number, quantity, production_date, total_cost) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setString(2, lotNumber);
            ps.setDouble(3, quantity);
            ps.setDate(4, productionDate);
            ps.setDouble(5, totalCost);
            ps.executeUpdate();
            System.out.println("Product lot added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateProductLot(int lotId, double quantity, double totalCost) {
        String sql = "UPDATE ProductLot SET quantity = ?, total_cost = ? WHERE lot_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, quantity);
            ps.setDouble(2, totalCost);
            ps.setInt(3, lotId);
            ps.executeUpdate();
            System.out.println("Product lot updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeProductLot(int lotId) {
        String sql = "DELETE FROM ProductLot WHERE lot_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lotId);
            ps.executeUpdate();
            System.out.println("Product lot removed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void readProductLots() {
        String sql = "SELECT * FROM ProductLot";
        try (Connection con = DatabaseConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("lot_id") + ", Product ID: " + rs.getInt("product_id") + ", Lot Number: " + rs.getString("lot_number") + ", Quantity: " + rs.getDouble("quantity") + ", Date: " + rs.getDate("production_date") + ", Total Cost: " + rs.getDouble("total_cost"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ProductLotAdditionalCost handling
    public void addAdditionalCost(int lotId, String description, double costAmount) {
        String sql = "INSERT INTO ProductLotAdditionalCost (lot_id, cost_description, cost_amount) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lotId);
            ps.setString(2, description);
            ps.setDouble(3, costAmount);
            ps.executeUpdate();
            System.out.println("Additional cost added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAdditionalCost(int costId, String description, double costAmount) {
        String sql = "UPDATE ProductLotAdditionalCost SET cost_description = ?, cost_amount = ? WHERE cost_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, description);
            ps.setDouble(2, costAmount);
            ps.setInt(3, costId);
            ps.executeUpdate();
            System.out.println("Additional cost updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeAdditionalCost(int costId) {
        String sql = "DELETE FROM ProductLotAdditionalCost WHERE cost_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, costId);
            ps.executeUpdate();
            System.out.println("Additional cost removed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<JSONObject> readAdditionalCost() {
        String sql = "SELECT * FROM ProductLotAdditionalCost";
        List<JSONObject> jsonList = new ArrayList<>(); // Create a list to hold the JSON objects

        try (Connection con = DatabaseConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("cost_id", rs.getInt("cost_id"));
                json.put("lot_id", rs.getInt("lot_id"));
                json.put("cost_description", rs.getString("cost_description"));
                json.put("cost_amount", rs.getDouble("cost_amount"));

                jsonList.add(json); // Add the JSON object to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jsonList; // Return the list of JSON objects
    }
}
