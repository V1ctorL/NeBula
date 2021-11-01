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
public class KafkaUtilException extends RuntimeException {

    public KafkaUtilException(String string) {
        super(string);
    }

    public KafkaUtilException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
    
}
