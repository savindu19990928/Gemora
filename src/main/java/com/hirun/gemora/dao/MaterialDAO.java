package com.hirun.gemora.dao;

import java.sql.PreparedStatement;
import com.hirun.gemora.DatabaseConnection;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {

    public static int addMaterial(String name, int unit, double unitPrice, int dmg) {
        String sql = "INSERT INTO Material (name, unit, unit_price, damaged_unit) VALUES (?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setInt(2, unit);
            ps.setDouble(3, unitPrice);
            ps.setInt(4, dmg);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1); // Retrieve generated ID
                    }
                }
            }

            System.out.println("Material added successfully with ID: " + generatedId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedId;
    }

    public static void updateMaterial(int materialId, String name, int unit, int dmg, double unitPrice) {
        String sql = "UPDATE Material SET name = ?, unit = ?, unit_price = ?, damaged_unit = ? WHERE material_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, unit);
            ps.setDouble(3, unitPrice);
            ps.setInt(4, dmg);
            ps.setInt(5, materialId);
            ps.executeUpdate();
            System.out.println("Material updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeMaterial(int materialId) {
        String sql = "DELETE FROM Material WHERE material_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            ps.executeUpdate();
            System.out.println("Material removed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readAllMaterials() {
        String sql = "SELECT name FROM Material"; // Fetch only the names
        List<String> nameList = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                nameList.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nameList; // Return the JSON array
    }

    public static JSONObject getMaterialById(int materialId) {
        String sql = "SELECT * FROM Material WHERE material_id = ?"; // Fetch material by ID
        JSONObject material = new JSONObject();

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, materialId); // Set the ID parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    material.put("material_id", rs.getInt("material_id"));
                    material.put("name", rs.getString("name"));
                    material.put("unit", rs.getInt("unit"));
                    material.put("unit_price", rs.getDouble("unit_price"));
                    material.put("damaged_unit", rs.getInt("damaged_unit"));
                    material.put("created_at", rs.getTimestamp("created_at"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return material; // Returns the material details as JSON
    }

    public static Integer getMaterialIdByName(String materialName) {
        String sql = "SELECT material_id FROM Material WHERE name = ?";
        Integer materialId = null;

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, materialName); // Set the name parameter
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    materialId = rs.getInt("material_id"); // Fetch the ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return materialId;
    }

    public static int addMaterialPurchase(int materialId, int supplierId, int quantity, double itemPrice) {
        String sql = "INSERT INTO MaterialPurchase (material_id, supplier_id, quantity, item_price) VALUES (?, ?, ?, ?)";
        int generatedPurchaseId = -1;

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, materialId);
            ps.setInt(2, supplierId);
            ps.setInt(3, quantity);
            ps.setDouble(4, itemPrice);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedPurchaseId = rs.getInt(1); // Retrieve generated Purchase ID
                    }
                }
            }

            System.out.println("Material purchase added successfully with ID: " + generatedPurchaseId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedPurchaseId;
    }

    // Method to update an existing material purchase
    public static void updateMaterialPurchase(int purchaseId, int materialId, int supplierId, int quantity, double itemPrice) {
        String sql = "UPDATE MaterialPurchase SET material_id = ?, supplier_id = ?, quantity = ?, item_price = ? WHERE purchase_id = ?";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            ps.setInt(2, supplierId);
            ps.setInt(3, quantity);
            ps.setDouble(4, itemPrice);
            ps.setInt(5, purchaseId);

            ps.executeUpdate();
            System.out.println("Material purchase updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getMaterialCostAndStock(int materialId) {
        String sql = "SELECT m.name, m.unit, m.unit_price, m.damaged_unit, SUM(mp.quantity * mp.item_price) AS total_purchase_cost "
                + "FROM Material m "
                + "LEFT JOIN MaterialPurchase mp ON m.material_id = mp.material_id "
                + "WHERE m.material_id = ? "
                + "GROUP BY m.material_id";

        JSONObject materialDetails = new JSONObject();

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, materialId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    materialDetails.put("name", rs.getString("name"));
                    materialDetails.put("unit", rs.getInt("unit"));
                    materialDetails.put("unit_price", rs.getDouble("unit_price"));
                    materialDetails.put("damaged_unit", rs.getInt("damaged_unit"));
                    materialDetails.put("total_purchase_cost", rs.getDouble("total_purchase_cost"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return materialDetails; // Returns the material details as JSON
    }

    public static Integer getFirstMaterialPurchaseIdByMaterialId(int materialId) {
        String sql = "SELECT mp.purchase_id "
                + "FROM MaterialPurchase mp "
                + "WHERE mp.material_id = ? "
                + "LIMIT 1";  // Limits the result to the first row

        Integer purchaseId = null;

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, materialId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    purchaseId = rs.getInt("purchase_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return purchaseId; // Returns the first purchase ID or null if not found
    }

    public static String getSupplierNameByPurchaseId(int purchaseId) {
        String sql = "SELECT s.name AS supplier_name "
                + "FROM MaterialPurchase mp "
                + "JOIN Supplier s ON mp.supplier_id = s.supplier_id "
                + "WHERE mp.purchase_id = ?";

        String supplierName = null;

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, purchaseId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    supplierName = rs.getString("supplier_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return supplierName; // Returns the supplier name as a string
    }
}
