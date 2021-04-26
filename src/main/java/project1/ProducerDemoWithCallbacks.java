package project1;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallbacks {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallbacks.class);
        String bootStrapServer = "127.0.0.1:9092";

        //create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);


        // send the data - async
        for (int i=0; i<20; i++){
            // create a producer record
            ProducerRecord<String, String> record = new ProducerRecord<>("first_topic", "whats up??" + Integer.toString(i));

            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    // EXECUTE every time a record is successfully sent or an exception is thrown
                    if (e == null){
                        // the record was sucessfully sent
                        logger.info("Received the metadata. \n" + "Topic: " +recordMetadata.topic() + "\n" + "Partitions: " + recordMetadata.partition() + "\n" + "Offset: " + recordMetadata.offset() + "\n" + "Timestamp: " + recordMetadata.timestamp());

                    } else {
                        logger.error("Error while producing", e);
                    }
                }
            });
        }


        // flush data
        producer.flush();

        // flush and close the producer
        producer.close();
    }
}
