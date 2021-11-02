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
import team.v1ctorl.nebula.Settings.Kafka;
import team.v1ctorl.nebula.exceptions.KafkaUtilException;

/**
 *
 * @author asus
 */
public class KafkaUtil {
    
    public static class Producer {
        private org.apache.kafka.clients.producer.Producer<String, String> producer;
        
        // The instance is declared volatile so that double check lock would work correctly.
        private static volatile Producer instance;

        private Producer() {
            Properties props = new Properties();
            props.put("bootstrap.servers", Kafka.BOOTSTRAP_SERVERS);
            props.put("acks", Kafka.Producer.ACKS);
            props.put("retries", Kafka.Producer.RETRIES);
            props.put("batch.size", Kafka.Producer.BATCH_SIZE);
            props.put("linger.ms", Kafka.Producer.LINGER_MS);
            props.put("buffer.memory", Kafka.Producer.BUFFER_MEMORY);
            props.put("key.serializer", Kafka.Producer.KEY_SERIALIZER);
            props.put("value.serializer", Kafka.Producer.VALUE_SERIALIZER);

            producer = new KafkaProducer<>(props);
        }
        
        public static Producer getInstance() {
            // double-checked locking
            Producer result = instance;
            if (result != null) {
                return result;
            }
            synchronized(Producer.class) {
                if (instance == null) {
                    instance = new Producer();
                }
                return instance;
            }
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
    
    public static class Consumer {
        private KafkaConsumer<String, String> consumer;
        
        // The instance is declared volatile so that double check lock would work correctly.
        private static volatile Consumer instance;

        private Consumer() {
            Properties props = new Properties();
            props.put("bootstrap.servers", Kafka.BOOTSTRAP_SERVERS);
            props.put("group.id", Kafka.Consumer.GROUP_ID);
            props.put("enable.auto.commit", Kafka.Consumer.ENABLE_AUTO_COMMIT);
            props.put("auto.offset.reset", Kafka.Consumer.AUTO_OFFSET_RESET);
            props.put("auto.commit.interval.ms", Kafka.Consumer.AUTO_COMMIT_INTERVAL_MS);
            props.put("key.deserializer", Kafka.Consumer.KEY_DESERIALIZER);
            props.put("value.deserializer", Kafka.Consumer.VALUE_DESERIALIZER);
            
            consumer = new KafkaConsumer(props);
        }
        
        public static Consumer getInstance() {
            // double-checked locking
            Consumer result = instance;
            if (result != null) {
                return result;
            }
            synchronized (Consumer.class) {
                if (instance == null) {
                    instance = new Consumer();
                }
                return instance;
            }
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
    
    public static void handleException(Exception ex, String exceptionInformation) {
        Logger.getLogger(KafkaUtil.class.getName()).log(Level.SEVERE, null, ex);
        throw new KafkaUtilException(exceptionInformation, ex);
    }
    
}
