/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team.v1ctorl.nebula;

/**
 *
 * @author asus
 */
public class Settings {
    public static final boolean DEVELOPER_MODE = false;
    
    public class Datebase {
        public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
        public static final String HOST = "localhost";
        public static final String NAME = "NeBula";
        public static final String USER = "root";
        public static final String PASSWORD = "123456";
    }
    
    public class Kafka {
        public static final boolean ENABLE_KAFKA = true;
        
        public static final String BOOTSTRAP_SERVERS = "192.168.137.108:9092";
        
        public class Producer {
            public static final String ACKS                     =   "all";
            public static final int RETRIES                     =   0;
            public static final int BATCH_SIZE                  =   16384;
            public static final int LINGER_MS                   =   1;
            public static final int BUFFER_MEMORY               =   33554432;
            public static final String KEY_SERIALIZER           =   "org.apache.kafka.common.serialization.StringSerializer";
            public static final String VALUE_SERIALIZER         =   "org.apache.kafka.common.serialization.StringSerializer";
        }
        
        public class Consumer {
            public static final String GROUP_ID                 =   "1";
            public static final String ENABLE_AUTO_COMMIT       =   "true";
            public static final String AUTO_OFFSET_RESET        =   "earliest";
            public static final String AUTO_COMMIT_INTERVAL_MS  =   "1000";
            public static final String KEY_DESERIALIZER         =   "org.apache.kafka.common.serialization.StringDeserializer";
            public static final String VALUE_DESERIALIZER       =   "org.apache.kafka.common.serialization.StringDeserializer";
            public static final long POLL_TIMEOUT               =   200;
        }
    }
}
