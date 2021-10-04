package com.bux.bot.basic_trading_bot.exception;

public class InvalidBodyRequestException extends Exception {
    private String field="";

    public InvalidBodyRequestException(String message) {
        super(message);
    }
    public InvalidBodyRequestException(String field,String message) {
        super(message);
        this.field=field;

    }
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
    @Override
    public String toString()
    {
       String superTostring= super.toString();
        if(!this.field.isEmpty())
        {
            superTostring=this.field+" is not valid:"+superTostring;
        }
        return superTostring;
    }
}
