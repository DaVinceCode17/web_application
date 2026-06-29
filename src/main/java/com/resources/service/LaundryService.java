package com.resources.service;

import com.resources.dao.CustomerDAO;
import com.resources.dao.OrderDAO;
import com.resources.dao.PricingDAO;
import com.resources.model.Customer;
import com.resources.model.Order;
import com.resources.model.Pricing;
import java.sql.SQLException;
import java.util.List;

public class LaundryService {
    
    private CustomerDAO customerDAO;
    private OrderDAO orderDAO;
    private PricingDAO pricingDAO;
    
    public LaundryService() {
        customerDAO = new CustomerDAO();
        orderDAO = new OrderDAO();
        pricingDAO = new PricingDAO();
    }
    
    public Customer login(String contact, String password) throws SQLException {
        return customerDAO.findByContactAndPassword(contact, password);
    }
    
    public boolean register(Customer customer) throws SQLException {
        if (customerDAO.findByContact(customer.getContact()) != null) {
            throw new IllegalArgumentException("Contact already registered");
        }
        return customerDAO.save(customer);
    }
    
    public List<Customer> getAllCustomers() throws SQLException {
        return customerDAO.findAll();
    }
    
    public Customer getCustomerById(int id) throws SQLException {
        return customerDAO.findById(id);
    }
    
    public Order createOrder(Order order) throws SQLException {
        order.setQueueNumber(orderDAO.getNextQueueNumber());
        order.setStatus("pending");
        return orderDAO.saveToPending(order);
    }
    
    public List<Order> getPendingOrders() throws SQLException {
        return orderDAO.getPending();
    }
    
    public List<Order> getOngoingOrders() throws SQLException {
        return orderDAO.getOngoing();
    }
    
    public List<Order> getPickupOrders() throws SQLException {
        return orderDAO.getPickup();
    }
    
    public List<Order> getDeliveredOrders() throws SQLException {
        return orderDAO.getDelivered();
    }
    
    public Order moveToOngoing(int orderId) throws SQLException {
        return orderDAO.moveToOngoing(orderId);
    }
    
    public Order moveToPickup(int orderId) throws SQLException {
        return orderDAO.moveToPickup(orderId);
    }
    
    public Order moveToDelivered(int orderId) throws SQLException {
        return orderDAO.moveToDelivered(orderId);
    }
    
    public List<Order> getOrdersByCustomer(int customerId) throws SQLException {
        return orderDAO.getOrdersByCustomer(customerId);
    }
    
    public Pricing getPricing() throws SQLException {
        Pricing p = pricingDAO.getPricing();
        if (p == null) {
            pricingDAO.insertDefaultPricing();
            p = pricingDAO.getPricing();
        }
        return p;
    }
    
    public boolean updatePricing(Pricing pricing) throws SQLException {
        return pricingDAO.updatePricing(pricing);
    }
}