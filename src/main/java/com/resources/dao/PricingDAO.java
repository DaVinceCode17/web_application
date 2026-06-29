package com.resources.dao;

import com.resources.model.Pricing;
import com.resources.DatabaseConnection;
import java.sql.*;

public class PricingDAO {
    
    public Pricing getPricing() throws SQLException {
        String sql = "SELECT * FROM pricing LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Pricing p = new Pricing();
                p.setId(rs.getInt("id"));
                p.setWashPrice(rs.getDouble("wash_price"));
                p.setDryPrice(rs.getDouble("dry_price"));
                p.setFoldPrice(rs.getDouble("fold_price"));
                return p;
            }
            return null;
        }
    }
    
    public boolean updatePricing(Pricing pricing) throws SQLException {
        String sql = "UPDATE pricing SET wash_price = ?, dry_price = ?, fold_price = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, pricing.getWashPrice());
            pstmt.setDouble(2, pricing.getDryPrice());
            pstmt.setDouble(3, pricing.getFoldPrice());
            pstmt.setInt(4, pricing.getId());
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public void insertDefaultPricing() throws SQLException {
        String sql = "INSERT INTO pricing (wash_price, dry_price, fold_price) VALUES (50, 30, 20)";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
}