package com.embeddedmeng.easywork.service;

import java.util.concurrent.Future;

public interface AsyncTaskService {
    Future<String> doTask1() throws InterruptedException;
    Future<String> doTask2() throws InterruptedException;
}
