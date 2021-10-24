/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team.v1ctorl.nebula.exceptions;

/**
 *
 * @author asus
 */
public class DbUtilException extends RuntimeException {

    public DbUtilException(String string) {
        super(string);
    }

    public DbUtilException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
    
}
