package com.vj.kafka.elastic.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
@Builder
@ToString
public class Account {
    private String accountId;
    private String accountName;
    private Double accountBalance;
    private List<Transaction> transactions;

    public boolean isValid() {
        boolean isValid = true;
        if (StringUtils.isEmpty(this.accountId) || StringUtils.isEmpty(this.accountName)) {
            isValid = false;
        }
        return isValid;
    }
}
