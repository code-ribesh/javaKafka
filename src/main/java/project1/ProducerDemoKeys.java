package project1;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;


public class ProducerDemoKeys {
    
    public static void main(String[] args) throws ExecutionException, InterruptedException {
         Logger logger = LoggerFactory.getLogger(ProducerDemoKeys.class);
         String bootStrapServer = "127.0.0.1:9092";

         // create a producer properties
         Properties properties = new Properties();
         properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
         properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
         properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

         // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);


        // send the data - async

        for (int i=0; i<10; i++){
            // create a producer record
            String topic = "first_topic";
            String value = " with the keys now " + Integer.toString(i);
            String key = "key_ " + Integer.toString(i);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value );

            logger.info("Key: " + key);
            // key = 0 -> partition 1
            // key = 1 -> partition 1
            // key = 2 -> partition 1
            // key = 3 -> partition 2
            // key = 4 -> partition 0
            // key = 5 -> partition 2
            // key = 6 -> partition 2
            // key = 7 -> partition 1
            // key = 8 -> partition 1
            // key = 9 -> partition 1
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    // EXECUTE every time a record is successfully sent or an exception is thrown
                    if (e == null){
                        // the record was successfully sent
                        logger.info("Received the metadata. \n" + "Topic: " +recordMetadata.topic() + "\n" + "Partitions: " + recordMetadata.partition() + "\n" + "Offset: " + recordMetadata.offset() + "\n" + "Timestamp: " + recordMetadata.timestamp());
                    }
                }
            }).get(); // block .send to make sync - dont do this at production
        }

        //flush data
        producer.flush();
        //flush and close the producer
        producer.close();


    }
}
