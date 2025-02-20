/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

class ProductMaterialDAO {

    public void addProductMaterial(int productId, int materialId, double quantityNeeded) {
        String sql = "INSERT INTO ProductMaterial (product_id, material_id, quantity_needed) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, materialId);
            ps.setDouble(3, quantityNeeded);
            ps.executeUpdate();
            System.out.println("Product material added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateProductMaterial(int productMaterialId, double quantityNeeded) {
        String sql = "UPDATE ProductMaterial SET quantity_needed = ? WHERE product_material_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, quantityNeeded);
            ps.setInt(2, productMaterialId);
            ps.executeUpdate();
            System.out.println("Product material updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeProductMaterial(int productMaterialId) {
        String sql = "DELETE FROM ProductMaterial WHERE product_material_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productMaterialId);
            ps.executeUpdate();
            System.out.println("Product material removed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<JSONObject> readProductMaterials() {
        String sql = "SELECT * FROM ProductMaterial";
        List<JSONObject> jsonList = new ArrayList<>(); // List to hold the JSON objects

        try (Connection con = DatabaseConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("product_material_id", rs.getInt("product_material_id"));
                json.put("product_id", rs.getInt("product_id"));
                json.put("material_id", rs.getInt("material_id"));
                json.put("quantity_needed", rs.getDouble("quantity_needed"));

                jsonList.add(json); // Add the JSON object to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jsonList; // Return the list of JSON objects
    }
}
