/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team.v1ctorl.nebula.threads;

import java.util.Arrays;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import team.v1ctorl.nebula.utils.KafkaUtil.Consumer;

/**
 *
 * @author asus
 */
public class KafkaConsumerThread extends Thread {

    @Override
    public void run() {
        Consumer consumer = Consumer.getInstance();
        consumer.subscribe(Arrays.asList("foo"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll();
            for (ConsumerRecord<String, String> record: records)
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
        }
    }
    
}
