/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team.v1ctorl.nebula.threads;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import team.v1ctorl.nebula.Settings;
import team.v1ctorl.nebula.utils.DbUtil;
import team.v1ctorl.nebula.utils.KafkaUtil.Consumer;

/**
 *
 * @author asus
 */
public class KafkaConsumerThread extends Thread {

    @Override
    public void run() {
        System.out.println("Running KafkaConsumerThread");
        Consumer consumer = Consumer.getInstance();
        consumer.subscribe(Arrays.asList("nebula"));
        DbUtil dbUtil = new DbUtil();
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll();
            for (ConsumerRecord<String, String> record: records) {
                if (record.key()!=null && !record.key().isEmpty()) {
                    if (record.key().equals(Settings.Kafka.Consumer.GROUP_ID))
                        System.out.print("From local machine:");
                    else
                        dbUtil.executeUpdate(record.value());
                }
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(KafkaProducerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
