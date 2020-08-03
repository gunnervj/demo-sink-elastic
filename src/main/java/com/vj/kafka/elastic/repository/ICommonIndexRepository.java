package com.vj.kafka.elastic.repository;

public interface ICommonIndexRepository {
    public boolean isIndexExist(String index);
    public boolean createEmptyIndex(String index);
}
