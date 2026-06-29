package com.resources.dao;

import com.resources.model.Order;
import com.resources.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    
    public Order saveToPending(Order order) throws SQLException {
        String sql = "INSERT INTO pending_orders (customer_id, services, service_type, queue_number, weight, price) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getCustomerId());
            pstmt.setString(2, order.getServices());
            pstmt.setString(3, order.getServiceType());
            pstmt.setInt(4, order.getQueueNumber());
            pstmt.setDouble(5, order.getWeight());
            pstmt.setDouble(6, order.getPrice());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) order.setId(rs.getInt(1));
                return order;
            }
            return null;
        }
    }
    
    public Order moveToOngoing(int orderId) throws SQLException {
        String selectSql = "SELECT * FROM pending_orders WHERE id = ?";
        Order order = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                order = mapPending(rs);
            }
        }
        if (order == null) return null;
        
        String insertSql = "INSERT INTO ongoing_orders (customer_id, services, service_type, queue_number, weight, price) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getCustomerId());
            pstmt.setString(2, order.getServices());
            pstmt.setString(3, order.getServiceType());
            pstmt.setInt(4, order.getQueueNumber());
            pstmt.setDouble(5, order.getWeight());
            pstmt.setDouble(6, order.getPrice());
            pstmt.executeUpdate();
        }
        
        String deleteSql = "DELETE FROM pending_orders WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();
        }
        
        return order;
    }
    
    public Order moveToPickup(int orderId) throws SQLException {
        String selectSql = "SELECT * FROM ongoing_orders WHERE id = ?";
        Order order = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                order = mapOngoing(rs);
            }
        }
        if (order == null) return null;
        
        String insertSql = "INSERT INTO pickup_orders (customer_id, services, service_type, queue_number, weight, price) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getCustomerId());
            pstmt.setString(2, order.getServices());
            pstmt.setString(3, order.getServiceType());
            pstmt.setInt(4, order.getQueueNumber());
            pstmt.setDouble(5, order.getWeight());
            pstmt.setDouble(6, order.getPrice());
            pstmt.executeUpdate();
        }
        
        String deleteSql = "DELETE FROM ongoing_orders WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();
        }
        
        return order;
    }
    
    public Order moveToDelivered(int orderId) throws SQLException {
        String selectSql = "SELECT * FROM pickup_orders WHERE id = ?";
        Order order = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                order = mapPickup(rs);
            }
        }
        if (order == null) return null;
        
        String insertSql = "INSERT INTO delivered_orders (customer_id, services, service_type, queue_number, weight, price) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getCustomerId());
            pstmt.setString(2, order.getServices());
            pstmt.setString(3, order.getServiceType());
            pstmt.setInt(4, order.getQueueNumber());
            pstmt.setDouble(5, order.getWeight());
            pstmt.setDouble(6, order.getPrice());
            pstmt.executeUpdate();
        }
        
        String deleteSql = "DELETE FROM pickup_orders WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();
        }
        
        return order;
    }
    
    public int getNextQueueNumber() throws SQLException {
        String sql = "SELECT COALESCE(MAX(queue_number), 0) + 1 FROM pending_orders";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
            return 1;
        }
    }
    
    public List<Order> getPending() throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.first_name, c.last_name FROM pending_orders o LEFT JOIN customers c ON o.customer_id = c.id ORDER BY o.queue_number ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapPendingWithName(rs));
        }
        return list;
    }
    
    public List<Order> getOngoing() throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.first_name, c.last_name FROM ongoing_orders o LEFT JOIN customers c ON o.customer_id = c.id ORDER BY o.queue_number ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapOngoingWithName(rs));
        }
        return list;
    }
    
    public List<Order> getPickup() throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.first_name, c.last_name FROM pickup_orders o LEFT JOIN customers c ON o.customer_id = c.id ORDER BY o.queue_number ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapPickupWithName(rs));
        }
        return list;
    }
    
    public List<Order> getDelivered() throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.first_name, c.last_name FROM delivered_orders o LEFT JOIN customers c ON o.customer_id = c.id ORDER BY o.queue_number ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapDeliveredWithName(rs));
        }
        return list;
    }
    
    public List<Order> getOrdersByCustomer(int customerId) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT 'pending' as status, o.*, c.first_name, c.last_name FROM pending_orders o LEFT JOIN customers c ON o.customer_id = c.id WHERE o.customer_id = ? " +
                     "UNION ALL " +
                     "SELECT 'ongoing' as status, o.*, c.first_name, c.last_name FROM ongoing_orders o LEFT JOIN customers c ON o.customer_id = c.id WHERE o.customer_id = ? " +
                     "UNION ALL " +
                     "SELECT 'pickup' as status, o.*, c.first_name, c.last_name FROM pickup_orders o LEFT JOIN customers c ON o.customer_id = c.id WHERE o.customer_id = ? " +
                     "UNION ALL " +
                     "SELECT 'delivered' as status, o.*, c.first_name, c.last_name FROM delivered_orders o LEFT JOIN customers c ON o.customer_id = c.id WHERE o.customer_id = ? " +
                     "ORDER BY queue_number ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, customerId);
            pstmt.setInt(3, customerId);
            pstmt.setInt(4, customerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order o = mapAllWithStatus(rs);
                list.add(o);
            }
        }
        return list;
    }
    
    private Order mapPending(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setCustomerId(rs.getInt("customer_id"));
        o.setServices(rs.getString("services"));
        o.setServiceType(rs.getString("service_type"));
        o.setQueueNumber(rs.getInt("queue_number"));
        o.setWeight(rs.getDouble("weight"));
        o.setPrice(rs.getDouble("price"));
        o.setCreatedAt(rs.getString("created_at"));
        o.setStatus("pending");
        return o;
    }
    
    private Order mapOngoing(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setCustomerId(rs.getInt("customer_id"));
        o.setServices(rs.getString("services"));
        o.setServiceType(rs.getString("service_type"));
        o.setQueueNumber(rs.getInt("queue_number"));
        o.setWeight(rs.getDouble("weight"));
        o.setPrice(rs.getDouble("price"));
        o.setCreatedAt(rs.getString("created_at"));
        o.setUpdatedAt(rs.getString("updated_at"));
        o.setStatus("ongoing");
        return o;
    }
    
    private Order mapPickup(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setCustomerId(rs.getInt("customer_id"));
        o.setServices(rs.getString("services"));
        o.setServiceType(rs.getString("service_type"));
        o.setQueueNumber(rs.getInt("queue_number"));
        o.setWeight(rs.getDouble("weight"));
        o.setPrice(rs.getDouble("price"));
        o.setCreatedAt(rs.getString("created_at"));
        o.setUpdatedAt(rs.getString("updated_at"));
        o.setStatus("pickup");
        return o;
    }
    
    private Order mapDelivered(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setCustomerId(rs.getInt("customer_id"));
        o.setServices(rs.getString("services"));
        o.setServiceType(rs.getString("service_type"));
        o.setQueueNumber(rs.getInt("queue_number"));
        o.setWeight(rs.getDouble("weight"));
        o.setPrice(rs.getDouble("price"));
        o.setCreatedAt(rs.getString("created_at"));
        o.setUpdatedAt(rs.getString("delivered_at"));
        o.setStatus("delivered");
        return o;
    }
    
    private Order mapAllWithStatus(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setCustomerId(rs.getInt("customer_id"));
        o.setServices(rs.getString("services"));
        o.setServiceType(rs.getString("service_type"));
        o.setQueueNumber(rs.getInt("queue_number"));
        o.setWeight(rs.getDouble("weight"));
        o.setPrice(rs.getDouble("price"));
        o.setCreatedAt(rs.getString("created_at"));
        o.setUpdatedAt(rs.getString("updated_at"));
        o.setStatus(rs.getString("status"));
        String fn = rs.getString("first_name");
        String ln = rs.getString("last_name");
        if (fn != null && ln != null) o.setCustomerName(fn + " " + ln);
        return o;
    }
    
    private Order mapPendingWithName(ResultSet rs) throws SQLException {
        Order o = mapPending(rs);
        String fn = rs.getString("first_name");
        String ln = rs.getString("last_name");
        if (fn != null && ln != null) o.setCustomerName(fn + " " + ln);
        return o;
    }
    
    private Order mapOngoingWithName(ResultSet rs) throws SQLException {
        Order o = mapOngoing(rs);
        String fn = rs.getString("first_name");
        String ln = rs.getString("last_name");
        if (fn != null && ln != null) o.setCustomerName(fn + " " + ln);
        return o;
    }
    
    private Order mapPickupWithName(ResultSet rs) throws SQLException {
        Order o = mapPickup(rs);
        String fn = rs.getString("first_name");
        String ln = rs.getString("last_name");
        if (fn != null && ln != null) o.setCustomerName(fn + " " + ln);
        return o;
    }
    
    private Order mapDeliveredWithName(ResultSet rs) throws SQLException {
        Order o = mapDelivered(rs);
        String fn = rs.getString("first_name");
        String ln = rs.getString("last_name");
        if (fn != null && ln != null) o.setCustomerName(fn + " " + ln);
        return o;
    }
}