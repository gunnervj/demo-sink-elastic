package com.vj.kafka.elastic.repository;

import com.vj.kafka.elastic.Constants;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;


@Slf4j
public class CommonIndexRepository implements  ICommonIndexRepository {
    private RestHighLevelClient elasticClient;

    public CommonIndexRepository(RestHighLevelClient elasticClient) {
        this.elasticClient = elasticClient;
    }

    public boolean isIndexExist(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            return elasticClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (Exception ex) {
            log.error("Error while checking if index [{}] exists.",index, ex);
        }

        return false;
    }

    public boolean createEmptyIndex(String index) {
        try {
            CreateIndexRequest request = new CreateIndexRequest(index);
            CreateIndexResponse response = elasticClient.indices().create(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (Exception ex) {
            log.error("Error while creating index [{}] exists.",index, ex);
        }

        return false;
    }


}
