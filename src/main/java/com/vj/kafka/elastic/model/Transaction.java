package com.vj.kafka.elastic.model;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Getter
@Builder
public class Transaction {
    private Long transactionDate;
    private Long transactionId;
    private Double amount;
    private String type;

    public boolean isValid() {
        boolean isValid = false;
        if (null != this.transactionDate  &&
                null != this.transactionId &&
                null != this.amount &&
                !StringUtils.isEmpty(this.type)) {
            if (this.amount > 0) {
                isValid = true;
            }
        }
        return isValid;
    }
}
