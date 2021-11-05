/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team.v1ctorl.nebula.threads;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import team.v1ctorl.nebula.utils.DbUtil;
import team.v1ctorl.nebula.utils.KafkaUtil.Producer;

/**
 *
 * @author asus
 */
public class KafkaProducerThread extends Thread {

    @Override
    public void run() {
        Producer producer = Producer.getInstance();
        DbUtil dbUtil = new DbUtil();
        while (true) {
            ResultSet rs = dbUtil.executeQuery("SELECT * FROM messages_to_send;");
            try {
                while (rs.next()) {
                    producer.send("nebula", rs.getString("key"), rs.getString("value"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(KafkaProducerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(KafkaProducerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
