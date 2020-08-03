package com.vj.kafka.elastic;

import com.vj.kafka.elastic.service.ElasticAccountTaskService;
import com.vj.kafka.elastic.service.TaskService;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;


public class MyElasticSinkTask extends SinkTask {

  private static Logger log = LoggerFactory.getLogger(MyElasticSinkTask.class);
  private RestHighLevelClient elasticClient;
  private TaskService taskService;

  private MyElasticSinkConnectorConfig config;

  @Override
  public void start(Map<String, String> settings) {
    this.config = new MyElasticSinkConnectorConfig(settings);
    initElasticClient();
    log.info("Elastic Index is {}", this.config.getElasticIndex());
    if (this.config.getElasticIndex().equalsIgnoreCase(Constants.ACCOUNT_INDEX)) {
      log.info("Initializing  ElasticAccountTaskService");
      this.taskService = new ElasticAccountTaskService(elasticClient);
    }
  }


  private void initElasticClient() {
    elasticClient =  new RestHighLevelClient(RestClient.builder(new HttpHost(this.config.getElasticHost(),
            this.config.getElasticHostPort(),
            this.config.getElasticProtocol())));
  }

  @Override
  public void put(Collection<SinkRecord> records) {
    try {
        if(null != records) {
          records.stream().filter(Objects::nonNull).map(sinkRecord ->String.valueOf(sinkRecord.value()))
                  .filter(Objects::nonNull)
                  .forEach( record -> {
                    log.info("Processing {}", record);
                    boolean isFailed = taskService.process(record);
                    if(isFailed) {
                      // push to a dead letter?
                    }
                  });
        }
    } catch (Exception ex) {
      log.error("Error while processing records", ex);
    }
  }

  @Override
  public void flush(Map<TopicPartition, OffsetAndMetadata> map) {

  }

  @Override
  public void stop() {
    if(this.elasticClient != null) {
      try {
        this.elasticClient.close();
      } catch (IOException e) {
        log.warn("Error while closing rest client");
      }
    }
  }

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }
}
