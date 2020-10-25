package com.embeddedmeng.easywork.service.impl;

import com.embeddedmeng.easywork.service.AsyncTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class AsyncTaskServiceImpl implements AsyncTaskService {

    private static Logger log = LoggerFactory.getLogger(AsyncTaskServiceImpl.class);

    @Async("myAsync")
    public Future<String> doTask1() throws InterruptedException {
        log.info("Task1 started.");
        long start = System.currentTimeMillis();
        Thread.sleep(5000);
        long end = System.currentTimeMillis();
        log.info("Task1 finished, time elapsed: {} ms.", end-start);
        return new AsyncResult<>("Task1 accomplished!");
    }

    @Async
    public Future<String> doTask2() throws InterruptedException {
        log.info("Task2 started.");
        long start = System.currentTimeMillis();
        Thread.sleep(3000);
        long end = System.currentTimeMillis();
        log.info("Task2 finished, time elapsed: {} ms.", end-start);
        return new AsyncResult<>("Task2 accomplished!");
    }

}
