/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team.v1ctorl.nebula.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import team.v1ctorl.nebula.models.Order;
import team.v1ctorl.nebula.models.ProductInAnOrder;
import team.v1ctorl.nebula.utils.DbUtil;
import team.v1ctorl.nebula.utils.SnowFlake;

/**
 *
 * @author asus
 */
@WebServlet("/order/*")
public class OrderServlet extends HttpServlet {
    DbUtil dbUtilAutoTransaction;
    DbUtil dbUtilManualTransaction;
    SnowFlake snowFlake;

    @Override
    public void init() throws ServletException {
        dbUtilAutoTransaction = new DbUtil();
        dbUtilManualTransaction = new DbUtil(false);
        snowFlake = new SnowFlake(0, 0);
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check whether the user has logged in or not
        HttpSession session = request.getSession(false);
        if (session==null || session.getAttribute("id")==null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // Get the user's ID
        String userID = (String) session.getAttribute("id");
        
        // Get the parameter in the URI
        String [] splitedURI = request.getRequestURI().split("/");
        
        if (splitedURI.length > 3) {
            // The request is asking for a specific order, search for it in the database
            ResultSet rs1 = dbUtilAutoTransaction.executeQuery("SELECT * FROM "
                    + "(SELECT * FROM orders WHERE user_id=" + userID + ") AS orders"
                    + " WHERE id=" + splitedURI[3]);
            try {
                if (rs1.next()) {
                    // Load data from ResultSet to a Java object
                    Order order = new Order();
                    order.setId(rs1.getLong("id"));
                    order.setDatetime(rs1.getDate("datetime"));
                    order.setTotalPrice(rs1.getFloat("total_price"));
                    
                    List<ProductInAnOrder> productList = new ArrayList<>();
                    ResultSet rs2 = dbUtilAutoTransaction.executeQuery("SELECT * FROM products_in_the_orders WHERE order_id=" + splitedURI[3]);
                    while (rs2.next()) {
                        ProductInAnOrder product = new ProductInAnOrder();
                        product.setProductID(rs2.getLong("product_id"));
                        product.setProductPrice(rs2.getFloat("product_price"));
                        product.setProductAmount(rs2.getInt("product_amount"));
                        product.setIsReturned(rs2.getBoolean("is_returned"));
                        
                        productList.add(product);
                    }
                    order.setProductList(productList);
                    
                    // Serialize
                    ObjectMapper objectMapper = new ObjectMapper();
                    String json = objectMapper.writeValueAsString(order);
                    
                    // Return response
                    response.getWriter().println(json);
                }
                else {
                    // The requested product is not found
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        else {
            // The request is not asking for a specific order, return the list of all orders of this user
            ResultSet rs1 = dbUtilAutoTransaction.executeQuery("SELECT * FROM orders WHERE user_id=" + userID);
            List<Order> orderList = new ArrayList<>();
            try {
                DbUtil dbUtil2 = new DbUtil();
                while (rs1.next()) {
                    // Load data from ResultSet to a Java object
                    Order order = new Order();
                    order.setId(rs1.getLong("id"));
                    order.setDatetime(rs1.getTimestamp("datetime"));
                    order.setTotalPrice(rs1.getFloat("total_price"));
                    
                    List<ProductInAnOrder> productList = new ArrayList<>();
                    ResultSet rs2 = dbUtil2.executeQuery("SELECT * FROM products_in_the_orders WHERE order_id=" + order.getId());
                    while (rs2.next()) {
                        ProductInAnOrder product = new ProductInAnOrder();
                        product.setProductID(rs2.getLong("product_id"));
                        product.setProductPrice(rs2.getFloat("product_price"));
                        product.setProductAmount(rs2.getInt("product_amount"));
                        product.setIsReturned(rs2.getBoolean("is_returned"));
                        
                        productList.add(product);
                    }
                    order.setProductList(productList);
                    
                    orderList.add(order);
                }
                dbUtil2.close();

                // Serialize
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(orderList);

                // Return response
                response.getWriter().println(json);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check whether the user has logged in or not
        HttpSession session = request.getSession(false);
        if (session==null || session.getAttribute("id")==null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // Check for the content type
        if (!request.getContentType().equalsIgnoreCase("application/json")) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }
        
        // Load data from the request
        String json = request.getReader().readLine();
        
        // Deserialize
        ObjectMapper objectMapper = new ObjectMapper();
        Order order = objectMapper.readValue(json, Order.class);
        
        // Create an order
        // Generate an ID
        long orderID = snowFlake.nextId();
        // Get the time when the ID is generated
        Date orderIDGeneratedDate = new Date(snowFlake.getLastTimestamp());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(orderIDGeneratedDate);
        // Get the user's ID
        String userID = (String) session.getAttribute("id");
        // Calculate the total price of the order
        int totalPrice = 0;
        // Insert data into database
        dbUtilManualTransaction.executeUpdate("INSERT INTO orders (id, datetime, user_id) VALUES (" + orderID + ", '" + date + "', " + userID + ");");
        // NOTICE: the currently inserted data dose NOT include the total price
        
        // Save the products of this order
        for (ProductInAnOrder product: order.getProductList()) {
            dbUtilManualTransaction.executeUpdate("INSERT INTO products_in_the_orders VALUES ("
                    + orderID + ", "
                    + product.getProductID() + ", "
                    + product.getProductPrice() + ", "
                    + product.getProductAmount() + ", false);"
            );
            totalPrice += product.getProductPrice() * product.getProductAmount();
        }
        
        // Update the total price in the order
        dbUtilManualTransaction.executeUpdate("UPDATE orders SET total_price=" + totalPrice + " WHERE id=" + orderID);
        
        dbUtilManualTransaction.commit();
        
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    public void destroy() {
        dbUtilAutoTransaction.close();
        dbUtilManualTransaction.close();
    }

}
