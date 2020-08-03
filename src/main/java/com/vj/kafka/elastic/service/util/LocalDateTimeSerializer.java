package com.vj.kafka.elastic.service.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.vj.kafka.elastic.Constants;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localDateTime));
    }
}
