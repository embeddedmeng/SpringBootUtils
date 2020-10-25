package com.embeddedmeng.easywork.controller;

import com.alibaba.fastjson.JSON;
import com.embeddedmeng.easywork.dto.KafkaMessage;
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
    public String sendMessage(@RequestBody KafkaMessage msg) {
        kafkaTemplate.send("demo", JSON.toJSONString(msg)); //使用kafka模板发送信息
        return "success";
    }

}
