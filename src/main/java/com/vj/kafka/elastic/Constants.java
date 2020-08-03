package com.vj.kafka.elastic;

import org.elasticsearch.common.Strings;

public class Constants {
    public static final String ACCOUNT_INDEX = "accounts";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String[] EXCLUDES = new String[] {"transactions"};
    public static final String[] INCLUDES = Strings.EMPTY_ARRAY;
}
