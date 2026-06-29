package com.resources.controller;

import com.resources.model.Order;
import com.resources.model.Pricing;
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

@WebServlet("/api/admin/*")
public class AdminController extends HttpServlet {
    
    private LaundryService service;
    
    @Override
    public void init() {
        service = new LaundryService();
    }
    
    // ===== GET PENDING ORDERS =====
    private void getPending(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        try {
            List<Order> orders = service.getPendingOrders();
            out.write("{\"success\":true,\"orders\":[");
            for (int i = 0; i < orders.size(); i++) {
                Order o = orders.get(i);
                out.write("{\"id\":" + o.getId() + ",\"customerName\":\"" + o.getCustomerName() + "\",\"services\":\"" + o.getServices() + "\",\"queueNumber\":" + o.getQueueNumber() + "}");
                if (i < orders.size() - 1) out.write(",");
            }
            out.write("]}");
        } catch (Exception e) {
            out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    // ===== GET ONGOING ORDERS =====
    private void getOngoing(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        try {
            List<Order> orders = service.getOngoingOrders();
            out.write("{\"success\":true,\"orders\":[");
            for (int i = 0; i < orders.size(); i++) {
                Order o = orders.get(i);
                out.write("{\"id\":" + o.getId() + ",\"customerName\":\"" + o.getCustomerName() + "\",\"services\":\"" + o.getServices() + "\",\"queueNumber\":" + o.getQueueNumber() + "}");
                if (i < orders.size() - 1) out.write(",");
            }
            out.write("]}");
        } catch (Exception e) {
            out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    // ===== GET PICKUP ORDERS (For Pick Up / To Deliver) =====
    private void getPickup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        try {
            List<Order> orders = service.getPickupOrders();
            out.write("{\"success\":true,\"orders\":[");
            for (int i = 0; i < orders.size(); i++) {
                Order o = orders.get(i);
                out.write("{\"id\":" + o.getId() + ",\"customerName\":\"" + o.getCustomerName() + "\",\"services\":\"" + o.getServices() + "\",\"queueNumber\":" + o.getQueueNumber() + "}");
                if (i < orders.size() - 1) out.write(",");
            }
            out.write("]}");
        } catch (Exception e) {
            out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    // ===== GET CLAIMED ORDERS (delivered_orders table) =====
    private void getClaimed(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        try {
            List<Order> orders = service.getDeliveredOrders();
            out.write("{\"success\":true,\"orders\":[");
            for (int i = 0; i < orders.size(); i++) {
                Order o = orders.get(i);
                out.write("{\"id\":" + o.getId() + ",\"customerName\":\"" + o.getCustomerName() + "\",\"services\":\"" + o.getServices() + "\",\"queueNumber\":" + o.getQueueNumber() + "}");
                if (i < orders.size() - 1) out.write(",");
            }
            out.write("]}");
        } catch (Exception e) {
            out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    // ===== MOVE ORDER =====
    private void moveOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        String json = readBody(req);
        
        try {
            int orderId = Integer.parseInt(extract(json, "orderId"));
            String action = extract(json, "action");
            
            Order result = null;
            if ("ongoing".equals(action)) {
                result = service.moveToOngoing(orderId);
            } else if ("pickup".equals(action)) {
                result = service.moveToPickup(orderId);
            } else if ("claimed".equals(action)) {
                result = service.moveToDelivered(orderId);
            }
            
            if (result != null) {
                out.write("{\"success\":true,\"order\":{\"id\":" + result.getId() + ",\"queueNumber\":" + result.getQueueNumber() + ",\"status\":\"" + action + "\"}}");
            } else {
                out.write("{\"success\":false,\"message\":\"Order not found\"}");
            }
        } catch (Exception e) {
            out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    // ===== GET PRICING =====
    private void getPricing(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        try {
            Pricing p = service.getPricing();
            out.write("{\"success\":true,\"pricing\":{\"id\":" + p.getId() + ",\"washPrice\":" + p.getWashPrice() + ",\"dryPrice\":" + p.getDryPrice() + ",\"foldPrice\":" + p.getFoldPrice() + "}}");
        } catch (Exception e) {
            out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    // ===== UPDATE PRICING =====
    private void updatePricing(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        String json = readBody(req);
        
        try {
            Pricing p = new Pricing();
            p.setId(Integer.parseInt(extract(json, "id")));
            p.setWashPrice(Double.parseDouble(extract(json, "washPrice")));
            p.setDryPrice(Double.parseDouble(extract(json, "dryPrice")));
            p.setFoldPrice(Double.parseDouble(extract(json, "foldPrice")));
            boolean success = service.updatePricing(p);
            out.write("{\"success\":" + success + ",\"message\":\"" + (success ? "Pricing updated" : "Update failed") + "\"}");
        } catch (Exception e) {
            out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if ("/pending".equals(path)) {
            getPending(req, resp);
        } else if ("/ongoing".equals(path)) {
            getOngoing(req, resp);
        } else if ("/pickup".equals(path)) {
            getPickup(req, resp);
        } else if ("/claimed".equals(path)) {
            getClaimed(req, resp);
        } else if ("/pricing".equals(path)) {
            getPricing(req, resp);
        } else {
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":false,\"message\":\"Invalid endpoint\"}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if ("/move".equals(path)) {
            moveOrder(req, resp);
        } else {
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":false,\"message\":\"Invalid endpoint\"}");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if ("/pricing".equals(path)) {
            updatePricing(req, resp);
        } else {
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":false,\"message\":\"Invalid endpoint\"}");
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