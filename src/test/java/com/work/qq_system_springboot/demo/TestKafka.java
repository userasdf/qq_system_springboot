package com.work.qq_system_springboot.demo;

import com.work.qq_system_springboot.QqSystemSpringbootApplication;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = QqSystemSpringbootApplication.class)
public class TestKafka {


    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void test() throws InterruptedException {
        kafkaProducer.sendMessage("testt","aefses");
        kafkaProducer.sendMessage("testt","asefaf");
        kafkaProducer.sendMessage("testt","sefsdfsdfs");
        kafkaProducer.sendMessage("testt","saefessf");
        Thread.sleep(3000);
    }

}

@Component
class KafkaProducer{
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic,String content){
        kafkaTemplate.send(topic,content);
    }
}

@Component
class KafkaConsumer{
    @KafkaListener(topics = {"testt"})
    public void handleMessage(ConsumerRecord record){
        System.out.println(record.value()+",,,");
    }
}