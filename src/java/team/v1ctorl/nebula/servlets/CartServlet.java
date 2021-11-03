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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import team.v1ctorl.nebula.models.Cart;
import team.v1ctorl.nebula.utils.DbUtil;

/**
 *
 * @author asus
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    DbUtil dbUtil;

    @Override
    public void init() throws ServletException {
        dbUtil = new DbUtil();
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
        HttpSession session = request.getSession(false);
        
        // Check whether the user has logged in or not.
        if (session==null || session.getAttribute("id")==null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // Load the cart for the user
        String userID = (String) session.getAttribute("id");
        ResultSet rs = dbUtil.executeQuery("SELECT * FROM cart WHERE user_id=" + userID);
        List<Cart> cartList = new ArrayList<>();
        try {
            while (rs.next()) {
                // Load data from ResultSet to a Java object.
                Cart cart = new Cart();
                cart.setUserID(Long.valueOf(userID));
                cart.setProductID(rs.getLong("product_id"));
                cart.setAmount(rs.getInt("amount"));
                
                cartList.add(cart);
            }
            // Serialize
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(cartList);

            // Return response
            response.getWriter().println(json);
        } catch (SQLException ex) {
            Logger.getLogger(CartServlet.class.getName()).log(Level.SEVERE, null, ex);
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
        HttpSession session = request.getSession(false);
        
        // Check whether the user has logged in or not.
        if (session==null || session.getAttribute("id")==null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // Load data from the request
        String contentType = request.getContentType();
        Long productID;
        Integer amount;
        if (contentType.equals("application/json")) {
            String json = request.getReader().readLine();
            
            // Deserialize
            ObjectMapper objectMapper = new ObjectMapper();
            Cart cart = objectMapper.readValue(json, Cart.class);
            
            productID = cart.getProductID();
            amount = cart.getAmount();
        }
        else {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }
        
        // Add a product in the cart of the user
        String userID = (String) session.getAttribute("id");
        if (amount == null)
            dbUtil.executeUpdate("INSERT INTO cart (user_id, product_id) VALUES (" + userID + ", " + productID + ");");
        else
            dbUtil.executeUpdate("INSERT INTO cart VALUES (" + userID + ", " + productID + ", " + amount + ");");
        
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    /**
     * Handles the HTTP <code>PUT</code> method.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    /**
     * Handles the HTTP <code>DELETE</code> method.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    public void destroy() {
        dbUtil.close();
    }

}
