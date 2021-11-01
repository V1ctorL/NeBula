/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team.v1ctorl.nebula.utils;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import team.v1ctorl.nebula.exceptions.KafkaUtilException;

/**
 *
 * @author asus
 */
public class KafkaUtil {
    
    public class Producer {
        org.apache.kafka.clients.producer.Producer<String, String> producer;

        public Producer() {
            Properties props = new Properties();
            props.put("bootstrap.servers", "192.168.137.108:9092");
            props.put("acks", "all");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

            producer = new KafkaProducer<>(props);
        }
        
        public void send(String topic, String key, String value) {
            try {
                Future<RecordMetadata> result = producer.send(new ProducerRecord<>(topic, key, value));
                RecordMetadata rm = result.get();
                System.out.println("topic: " + rm.topic() + ", partition: " + rm.partition() + ", offset: " + rm.offset());
            } catch (InterruptedException | ExecutionException ex) {
                handleException(ex, "Met exception while sending message.");
            }
        }
        
        public void close() {
            producer.close();
        }
        
    }
    
    public class Consumer {
        KafkaConsumer<String, String> consumer;

        public Consumer() {
            Properties props = new Properties();
            props.put("bootstrap.servers", "192.168.137.108:9092");
            props.put("group.id", "1");
            props.put("enable.auto.commit", "true");
            props.put("auto.offset.reset", "earliest");
            props.put("auto.commit.interval.ms", "1000");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            
            consumer = new KafkaConsumer(props);
        }
        
        public void subscribe(Collection<String> topics) {
            consumer.subscribe(topics);
        }
        
        public ConsumerRecords<String, String> poll() {
            return consumer.poll(200);
        }
        
        public void close() {
            consumer.close();
        }
        
    }
    
    public void handleException(Exception ex, String exceptionInformation) {
        Logger.getLogger(KafkaUtil.class.getName()).log(Level.SEVERE, null, ex);
        throw new KafkaUtilException(exceptionInformation, ex);
    }
    
}
