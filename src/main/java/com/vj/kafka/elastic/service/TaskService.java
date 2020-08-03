package com.vj.kafka.elastic.service;

public interface TaskService {
    public boolean process(String record);
}
