package com.embeddedmeng.easywork;

import com.alibaba.fastjson.JSON;
import com.embeddedmeng.easywork.dto.KafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class EasyworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyworkApplication.class, args);
    }

    /**
     * 订阅kafka消息
     * 定义此消费者接收topics = "demo"的消息，与controller中的topic对应上即可
     * @param record 变量代表消息本身，可以通过ConsumerRecord<?,?>类型的record变量来打印接收的消息的各种信息
     */
    @KafkaListener(topics = "demo")
    public void listen (ConsumerRecord<?, ?> record){
        KafkaMessage kafkaMessage = JSON.parseObject((String) record.value(), KafkaMessage.class);
        System.out.println(kafkaMessage.toString());
        System.out.printf("topic is %s, offset is %d, value is %s \n", record.topic(), record.offset(), record.value());
    }

}
