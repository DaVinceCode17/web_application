package com.resources.controller;

import com.resources.model.Order;
import com.resources.service.LaundryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/orders/*")
public class OrderController extends HttpServlet {
    
    private LaundryService service;
    
    @Override
    public void init() {
        service = new LaundryService();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        
        if ("/create".equals(path)) {
            String json = readBody(req);
            try {
                int customerId = Integer.parseInt(extract(json, "customerId"));
                String services = extract(json, "services");
                String serviceType = extract(json, "serviceType");
                Order o = new Order(customerId, services, serviceType);
                Order created = service.createOrder(o);
                out.write("{\"success\":true,\"order\":{\"id\":" + created.getId() + ",\"queueNumber\":" + created.getQueueNumber() + ",\"status\":\"" + created.getStatus() + "\"}}");
            } catch (Exception e) {
                out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
            }
        } else if ("/update-status".equals(path)) {
            String json = readBody(req);
            try {
                int orderId = Integer.parseInt(extract(json, "orderId"));
                String status = extract(json, "status");
                Order o = service.moveToOngoing(orderId);
                out.write("{\"success\":true,\"order\":{\"id\":" + o.getId() + ",\"status\":\"" + o.getStatus() + "\"}}");
            } catch (Exception e) {
                out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
            }
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        
        try {
            if (path != null && path.startsWith("/customer/")) {
                int id = Integer.parseInt(path.substring(10));
                List<Order> orders = service.getOrdersByCustomer(id);
                out.write("{\"success\":true,\"orders\":[");
                for (int i = 0; i < orders.size(); i++) {
                    Order o = orders.get(i);
                    out.write("{\"id\":" + o.getId() + ",\"customerId\":" + o.getCustomerId() + ",\"customerName\":\"" + o.getCustomerName() + "\",\"services\":\"" + o.getServices() + "\",\"serviceType\":\"" + o.getServiceType() + "\",\"status\":\"" + o.getStatus() + "\",\"queueNumber\":" + o.getQueueNumber() + ",\"createdAt\":\"" + o.getCreatedAt() + "\"}");
                    if (i < orders.size() - 1) out.write(",");
                }
                out.write("]}");
            } else if ("/all".equals(path)) {
                List<Order> orders = service.getPendingOrders();
                out.write("{\"success\":true,\"orders\":[");
                for (int i = 0; i < orders.size(); i++) {
                    Order o = orders.get(i);
                    out.write("{\"id\":" + o.getId() + ",\"customerId\":" + o.getCustomerId() + ",\"customerName\":\"" + o.getCustomerName() + "\",\"services\":\"" + o.getServices() + "\",\"serviceType\":\"" + o.getServiceType() + "\",\"status\":\"" + o.getStatus() + "\",\"queueNumber\":" + o.getQueueNumber() + ",\"createdAt\":\"" + o.getCreatedAt() + "\"}");
                    if (i < orders.size() - 1) out.write(",");
                }
                out.write("]}");
            } else {
                out.write("{\"success\":false,\"message\":\"Invalid endpoint\"}");
            }
        } catch (Exception e) {
            out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        
        if (path != null && path.contains("/status")) {
            String[] parts = path.split("/");
            int orderId = Integer.parseInt(parts[1]);
            String json = readBody(req);
            try {
                String status = extract(json, "status");
                Order o = service.moveToOngoing(orderId);
                out.write("{\"success\":true,\"order\":{\"id\":" + o.getId() + ",\"status\":\"" + o.getStatus() + "\"}}");
            } catch (Exception e) {
                out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
            }
        }
    }
    
    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = req.getReader()) {
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
    
    private String extract(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        if (json.charAt(start) == '"') {
            start++;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        }
        int end = start;
        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
        return json.substring(start, end).trim();
    }
}