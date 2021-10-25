/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team.v1ctorl.nebula.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import team.v1ctorl.nebula.utils.DbUtil;

/**
 *
 * @author asus
 */
@WebServlet("/product/*")
public class ProductServlet extends HttpServlet {
    DbUtil dbUtil;

    @Override
    public void init() throws ServletException {
        dbUtil = new DbUtil();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        String requestURI = request.getRequestURI();
        String [] splitedURI = requestURI.split("/");
        
        PrintWriter out = response.getWriter();
        
        if (splitedURI.length > 3) {
            // The request is asking for a specific product, search for it in the database.
            ResultSet rs = dbUtil.executeQuery("SELECT * FROM products WHERE id=" + splitedURI[3]);
            try {
                if (rs.next()) {
                    // The requested product is found.
                    out.println("found the product");  // TODO: use Jackson to handle json
                }
                else {
                    // The requested product is not found.
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException ex) {
                throw new RuntimeException("Met SQLException while searching for a product.");
            }
        }
        else {
            // The request is not asking for a specific product, return the list of all products.
            out.println("list");  // TODO: use Jackson to handle json
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
        String name = request.getParameter("name");
        String price = request.getParameter("price");
        String description = request.getParameter("description");  // The maximum length of a String object and that of TEXT in MySQL are equal, which is 65535.
        
        PrintWriter out = response.getWriter();
        
        if (description == null) {
            dbUtil.executeUpdate("INSERT INTO products (name, price) VALUES ('" + name + "', " + price + ");");
        }
        else {
            StringBuffer sb = new StringBuffer();  // StringBuffer is thread-safe, though StringBuilder is faster.
            sb
                    .append("INSERT INTO products (name, price, description) VALUES ('")
                    .append(name).append("', ")
                    .append(price).append(", '")
                    .append(description.replaceAll("'", "''"))  // Handle single quotation marks
                    .append("');");
            dbUtil.executeUpdate(sb.toString());
        }
        response.setStatus(HttpServletResponse.SC_CREATED);
        
        // TODO: update resource
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
        String requestURI = request.getRequestURI();
        String [] splitedURI = requestURI.split("/");
        
        PrintWriter out = response.getWriter();
        
        if (splitedURI.length > 3) {
            // The request is trying to delete a specific product, search for it in the database.
            ResultSet rs = dbUtil.executeQuery("SELECT * FROM products WHERE id=" + splitedURI[3]);
            try {
                if (rs.next()) {
                    // The requested product is found.
                    dbUtil.executeUpdate("DELETE FROM products WHERE id=" + splitedURI[3]);
                }
                else {
                    // The requested product is not found.
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException ex) {
                throw new RuntimeException("Met SQLException while deleting a product.");
            }
        }
        else {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    @Override
    public void destroy() {
        dbUtil.close();
    }

}
