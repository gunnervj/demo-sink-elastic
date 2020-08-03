package com.vj.kafka.elastic;

import com.vj.kafka.elastic.validator.PortValidator;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Importance;

import java.util.Map;


public class MyElasticSinkConnectorConfig extends AbstractConfig {

    public static final String ELASTIC_INDEX = "elastic.index.name";
    private static final String ELASTIC_INDEX_DOC = "Elastic Search index to write data to";

    public static final String ELASTIC_HOST = "elastic.host.name";
    private static final String ELASTIC_HOST_DOC = "Elastic Search server host name or IP. If not provided, will default to localhost";

    public static final String ELASTIC_HOST_PORT = "elastic.host.port";
    private static final String ELASTIC_HOST_PORT_DOC = "Elastic Search server host port. If not provided, defaults to 9200";

    public static final String ELASTIC_USER = "elastic.username";
    private static final String ELASTIC_USER_DOC = "Elastic Search User Name for login. Defaults to 'user'";

    public static final String ELASTIC_PASSWORD = "elastic.password";
    private static final String ELASTIC_PASSWORD_DOC = "Elastic Search Password for login. Defaults to 'password'";

    public static final String ELASTIC_PROTOCOL = "elastic.protocol";
    private static final String ELASTIC_PROTOCOL_DOC = "Elastic Search Server Protocol. Defaults to 'http'";


    public MyElasticSinkConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
        super(config, parsedConfig);
    }

    public MyElasticSinkConnectorConfig(Map<?, ?> parsedConfig) {
        super(config(), parsedConfig);
    }

    public static ConfigDef config() {
        return new ConfigDef()
                .define(ELASTIC_INDEX, Type.STRING, Importance.HIGH, ELASTIC_INDEX_DOC)
                .define(ELASTIC_HOST, Type.STRING, "localhost", Importance.MEDIUM, ELASTIC_HOST_DOC)
                .define(ELASTIC_HOST_PORT, Type.INT, 9200, new PortValidator(), Importance.MEDIUM, ELASTIC_HOST_PORT_DOC)
                .define(ELASTIC_USER, Type.STRING, "user", Importance.MEDIUM, ELASTIC_USER_DOC)
                .define(ELASTIC_PASSWORD, Type.STRING, "password", Importance.MEDIUM, ELASTIC_PASSWORD_DOC)
                .define(ELASTIC_PROTOCOL, Type.STRING, "http", Importance.LOW, ELASTIC_PROTOCOL_DOC);
    }

    public String getElasticIndex() {
        return this.getString(ELASTIC_INDEX);
    }

    public String getElasticHost() {
        return this.getString(ELASTIC_HOST);
    }

    public Integer getElasticHostPort() {
        return this.getInt(ELASTIC_HOST_PORT);
    }

    public String getElasticUser() {
        return this.getString(ELASTIC_USER);
    }

    public String getElasticPassword() {
        return this.getString(ELASTIC_PASSWORD);
    }

    public String getElasticProtocol() {
        return this.getString(ELASTIC_PROTOCOL);
    }

}
