/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team.v1ctorl.nebula.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import team.v1ctorl.nebula.threads.KafkaConsumerThread;
import team.v1ctorl.nebula.threads.KafkaProducerThread;

/**
 * Web application lifecycle listener.
 *
 * @author asus
 */
public class KafkaListener implements ServletContextListener {
    private KafkaConsumerThread kafkaConsumerThread;
    private KafkaProducerThread kafkaProducerThread;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (kafkaConsumerThread == null) {
            kafkaConsumerThread = new KafkaConsumerThread();
            kafkaConsumerThread.start();
        }
        
        if (kafkaProducerThread == null) {
            kafkaProducerThread = new KafkaProducerThread();
            kafkaProducerThread.start();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (kafkaConsumerThread != null && kafkaConsumerThread.isAlive())
            kafkaConsumerThread.interrupt();
        
        if (kafkaProducerThread != null && kafkaProducerThread.isAlive())
            kafkaProducerThread.interrupt();
    }
}
