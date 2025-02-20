package com.hirun.gemora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.hirun.gemora.DatabaseConnection;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.json.JSONObject;

public class ProductLotDAO {

    public static int addProductLot(String name, int quantity, int damagedUnit) {
        String sql = "INSERT INTO ProductLot (name, quantity, damaged_unit) VALUES (?, ?, ?)";
        int generatedId = -1;
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setInt(3, damagedUnit);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    public static boolean updateProductLot(int lotId, String name, int quantity, int damagedUnit) {
        String sql = "UPDATE ProductLot SET name = ?, quantity = ?, damaged_unit = ? WHERE lot_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setInt(3, damagedUnit);
            ps.setInt(4, lotId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private static double getTotalLotCost(int lotId) {
        double totalCost = 0.00;
        String query = "SELECT "
                + "SUM(lsd.quantity_sold * lsd.item_Price) AS total_sale_cost, "
                + "IFNULL(SUM(psac.cost_amount), 0) AS additional_cost, "
                + "(SUM(lsd.quantity_sold * lsd.item_Price) + IFNULL(SUM(psac.cost_amount), 0)) AS total_lot_cost "
                + "FROM LotSaleDetails lsd "
                + "LEFT JOIN ProductSaleAdditionalCost psac ON lsd.lot_id = psac.lot_id "
                + "WHERE lsd.lot_id = ? "
                + // Filter by specific lot_id
                "GROUP BY lsd.lot_id";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, lotId); // Set the lot_id parameter

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalCost = rs.getDouble("total_lot_cost"); // Fetch total cost
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalCost;
    }

    public static void loadProductLotsIntoTable(DefaultTableModel tableModel, int saleId) {
        String query = "SELECT pl.lot_id, pl.name, pl.quantity, pl.damaged_unit, pl.production_date "
                + "FROM ProductLot pl "
                + "JOIN LotSales ls ON pl.lot_id = ls.lot_id "
                + "WHERE ls.sale_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, saleId);
            try (ResultSet rs = stmt.executeQuery()) {
                tableModel.setRowCount(0);
                while (rs.next()) {
                    int id = rs.getInt("lot_id");
                    String name = rs.getString("name");
                    int quantity = rs.getInt("quantity");
                    double totalCost = getTotalLotCost(id);

                    tableModel.addRow(new Object[]{id, name, quantity, totalCost});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeProductLot(int lotId) {
        String sql = "DELETE FROM ProductLot WHERE lot_id = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lotId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<JSONObject> readProductLots() {
        String sql = "SELECT * FROM ProductLot";
        List<JSONObject> productLots = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("lot_id", rs.getInt("lot_id"));
                json.put("product_id", rs.getInt("product_id"));
                json.put("name", rs.getString("name"));
                json.put("quantity", rs.getInt("quantity"));
                json.put("damaged_unit", rs.getInt("damaged_unit"));
                productLots.add(json);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productLots;
    }

    public static int getLotIdByName(String lotName) {
        String query = "SELECT lot_id FROM ProductLot WHERE name = ?";
        int lotId = -1; // Default value if not found

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, lotName); // Set lot name parameter

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    lotId = rs.getInt("lot_id"); // Fetch the lot ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lotId;
    }

    public static JSONObject readProductLotById(int lotId) {
        String sql = "SELECT * FROM ProductLot WHERE lot_id = ?";
        JSONObject json = new JSONObject();
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lotId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                json.put("lot_id", rs.getInt("lot_id"));
                json.put("name", rs.getString("name"));
                json.put("quantity", rs.getInt("quantity"));
                json.put("damaged_unit", rs.getInt("damaged_unit"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return json;
    }
}
