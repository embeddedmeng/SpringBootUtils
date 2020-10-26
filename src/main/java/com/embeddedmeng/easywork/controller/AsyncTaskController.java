package com.embeddedmeng.easywork.controller;

import com.embeddedmeng.easywork.controller.base.BaseController;
import com.embeddedmeng.easywork.service.AsyncTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Api(tags = "异步任务")
@RestController
@RequestMapping(value = "/task")
public class AsyncTaskController extends BaseController {

    @Resource
    private AsyncTaskService asyncTask;

    @GetMapping("/async")
    @ApiOperation("异步任务调用测试接口")
    public String task() throws InterruptedException, ExecutionException {
        Long time = System.currentTimeMillis();
        Future<String> task1 = asyncTask.doTask1();
        Future<String> task2 = asyncTask.doTask2();

        while(true) {
            if(task1.isDone() && task2.isDone()) {
                logger.info("Task1 result: {}", task1.get());
                logger.info("Task2 result: {}", task2.get());
                break;
            }
            Thread.sleep(1000);
        }
        logger.info("耗时:{} ms",System.currentTimeMillis()-time);
        return "success";
    }

}
