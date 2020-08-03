package com.vj.kafka.elastic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyElasticSinkConnector extends SinkConnector {

  private static Logger log = LoggerFactory.getLogger(MyElasticSinkConnector.class);
  private MyElasticSinkConnectorConfig config;

  @Override
  public List<Map<String, String>> taskConfigs(int maxTasks) {
    List<Map<String, String>> taskConfigs = new ArrayList<>();
    for(int i=0; i< maxTasks ; i++) {
      taskConfigs.add(config.originalsStrings());
    }
    return taskConfigs;
  }

  @Override
  public void start(Map<String, String> connectorSettings) {
    log.info("------ Starting My Elastic Sink Connector -------");
    config = new MyElasticSinkConnectorConfig(connectorSettings);
  }

  @Override
  public void stop() {
    log.info("------ Stopping My Elastic Sink Connector -------");
  }

  @Override
  public ConfigDef config() {
    return MyElasticSinkConnectorConfig.config();
  }

  @Override
  public Class<? extends Task> taskClass() {
    return MyElasticSinkTask.class;
  }

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }
}
