package com.embeddedmeng.easywork.controller;

import com.alibaba.fastjson.JSON;
import com.embeddedmeng.easywork.controller.base.BaseController;
import com.embeddedmeng.easywork.dto.KafkaMessage;
import io.swagger.annotations.Api;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "kafka测试")
@RestController
@RequestMapping(value = "/kafka")
public class KafkaController extends BaseController {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("sendMessage")
    public String sendMessage(@RequestBody KafkaMessage msg) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("demo", JSON.toJSONString(msg)); //使用kafka模板发送信息
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.info(result.toString() + "发送成功");
            }
            @Override
            public void onFailure(Throwable ex) {
                logger.error("发送失败", ex);
            }
        });
        return "success";
    }

}
