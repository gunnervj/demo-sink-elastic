package com.vj.kafka.elastic.service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class SourceDataModel {
    private RequestType requestType;
    private String accountId;
    private String accountName;
    private Double accountBalance;
    private LocalDateTime transactionDate;
    private Long transactionId;
    private Double amount;
    private String type;
}
