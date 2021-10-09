package com.bux.bot.basic_trading_bot.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ValidationError implements Serializable {
    private String field;
    private String userError;
    private String developerError;

    public ValidationError(String field, String userError) {
        this.field = field;
        this.userError = userError;
        this.developerError=userError;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(field).
                append(userError).
                append(developerError).
                toHashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if(obj==null)return false;
        return this.hashCode()==obj.hashCode();
    }

}
