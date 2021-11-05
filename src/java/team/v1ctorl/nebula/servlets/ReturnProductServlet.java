/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team.v1ctorl.nebula.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import team.v1ctorl.nebula.Settings;
import team.v1ctorl.nebula.utils.DbUtil;

/**
 *
 * @author asus
 */
@WebServlet("/return/*")
public class ReturnProductServlet extends HttpServlet {
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
        if (Settings.DEVELOPER_MODE) doPost(request, response);
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
        
        // Get the parameter in the URI
        String [] splitedURI = request.getRequestURI().split("/");
        
        // Check whether the parameters are provided in the URI
        if (splitedURI.length < 4) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        String orderID = splitedURI[3];
        String productID = splitedURI[4];
        
        dbUtil.executeUpdate("UPDATE products_in_the_orders SET is_returned=TRUE"
                + " WHERE order_id=" + orderID + " AND product_id=" + productID);
        
        response.setStatus(HttpServletResponse.SC_RESET_CONTENT);
    }

    @Override
    public void destroy() {
        dbUtil.close();
    }

}
