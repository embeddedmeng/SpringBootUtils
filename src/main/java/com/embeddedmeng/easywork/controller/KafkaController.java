package com.embeddedmeng.easywork.controller;

import io.swagger.annotations.Api;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "kafka测试")
@RestController
@RequestMapping(value = "/kafka")
public class KafkaController {

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("sendMessage")
    public String sendMessage(@RequestBody String msg){
        kafkaTemplate.send("demo", msg); //使用kafka模板发送信息
        return "success";
    }

}
