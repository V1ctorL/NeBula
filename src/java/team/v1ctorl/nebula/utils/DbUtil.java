/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team.v1ctorl.nebula.utils;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import team.v1ctorl.nebula.Settings.Datebase;
import team.v1ctorl.nebula.exceptions.DbUtilException;

/**
 *
 * @author asus
 */
public class DbUtil {
    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    private static final String JDBC_DRIVER = Datebase.DRIVER;
    private static final String DB_URL = "jdbc:mysql://" + Datebase.HOST + ":3306/" + Datebase.NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
 
    // 数据库的用户名与密码
    private static final String USER = Datebase.USER;
    private static final String PASS = Datebase.PASSWORD;
    
    // 数据库操作相关变量
    private Connection conn;
    private Statement stmt;
    private PreparedStatement pstmt;

    public DbUtil() {
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);
        
            // 打开链接
            System.out.println("Connecting to the database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            stmt = conn.createStatement();
            System.out.println("Connected to the database.");
        } catch (SQLException ex) {
            handleException(ex, "Fail to get connected to the database.");
        } catch (ClassNotFoundException ex) {
            handleException(ex, "Fail to load jdbc driver.");
        }
    }
    
    public void setAutoCommit(boolean autoCommit) {
        try {
            // If set false, a lock will be gotten, which MUST be released by setting it true after commit.
            conn.setAutoCommit(autoCommit);
            stmt = conn.createStatement();
        } catch (SQLException ex) {
            handleException(ex, "Met exception while setting auto commit.");
        }
    }

    public void setPreparedStatement(PreparedStatement pstmt) {
        this.pstmt = pstmt;
    }
    
    public PreparedStatement prepareStatement(String sql) {
        try {
            pstmt = conn.prepareStatement(sql);
            return pstmt;
        } catch (SQLException ex) {
            handleException(ex, "Met exception while preparing statement.");
            return null;
        }
    }
    
    public ResultSet executeQuery(String sql) {
        try {
            return stmt.executeQuery(sql);
        } catch (SQLException ex) {
            handleException(ex, "Met exception while executing query.");
            return null;
        }
    }
    
    public int executeUpdate(String sql) {
        try {
            return stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            handleException(ex, "Met exception while executing update.");
            return -1;
        }
    }
    
    public int executeUpdate() {
        try {
            return pstmt.executeUpdate();
        } catch (SQLException ex) {
            handleException(ex, "Met exception while executing update.");
            return -1;
        }
    }
    
    public void commit() {
        try {
            conn.commit();
        } catch (SQLException ex) {
            try {
                conn.rollback();
            } catch (SQLException ex1) {
                handleException(ex, "Met exception while rolling back.");
            }
            handleException(ex, "Met exception while committing.");
        } finally {
            this.setAutoCommit(false);
        }
    }
    
    public void close() {
        try {
            if (!conn.getAutoCommit()) this.commit();
            if (pstmt != null) pstmt.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            handleException(ex, "Met exception while closing statement or connection.");
        }
    }
    
    public static void handleException(Exception ex, String exceptionInformation) {
        Logger.getLogger(DbUtil.class.getName()).log(Level.SEVERE, null, ex);
        throw new DbUtilException(exceptionInformation, ex);
    }
}
