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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import team.v1ctorl.nebula.Settings;
import team.v1ctorl.nebula.utils.BCrypt;
import team.v1ctorl.nebula.utils.DbUtil;
import team.v1ctorl.nebula.utils.SnowFlake;

/**
 *
 * @author asus
 */
@WebServlet("/register")
public class UserRegisterServlet extends HttpServlet {
    DbUtil dbUtil;
    SnowFlake snowFlake;

    @Override
    public void init() throws ServletException {
        dbUtil = new DbUtil();
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        
        // Check whether the user has logged in or not.
        if (session!=null && session.getAttribute("id")!=null) {
            out.println("User has logged in.");
            return;
        }
        
        String username = new String(request.getParameter("username").getBytes("ISO8859-1"), "UTF-8"); // handle Chinese
        String password = request.getParameter("password");
        
        ResultSet rs = dbUtil.executeQuery("SELECT * FROM users WHERE username='" + username + "';");
        try {
            if (rs.next()) {
                out.println("This user name has already been registered, please select another name.");
            }
            else {
                // Generate an ID
                long userID = snowFlake.nextId();
                
                // Encrypt the password
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                
                // Create a new record in the datebase
                dbUtil.executeUpdate("INSERT INTO users VALUES (" + userID + ", '" + username + "', '" + hashedPassword + "');");
                
                // Make user log in after registered.
                session = request.getSession();
                session.setAttribute("id", userID);
                
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.println("User " + username + " is successfully registered.");
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Met SQLException while checking username that already exists.");
        }
    }

    @Override
    public void destroy() {
        dbUtil.close();
    }

}
