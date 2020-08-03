package com.vj.kafka.elastic.validator;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

public class PortValidator implements ConfigDef.Validator {

    @Override
    public void ensureValid(String name, Object value) {
        Integer port = (Integer) value;
        if( 1000 > port || 10000 < port ) {
            throw new ConfigException(name, value, "Invalid port");
        }
    }

}
