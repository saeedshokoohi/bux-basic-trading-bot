package com.bux.bot.basic_trading_bot.service.constants;

public  final class ValidationMessages {


    public static final String NULL_VALUE_IS_NOT_VALID = "Null value is not valid";
    public static final String LOWER_SELL_PRICE_MUST_BE_LOWER_THAN_BUY_PRICE = "lower sell price must be lower than buy price";
    public static final String UPPER_SELL_PRICE_MUST_BE_HIGHER_THAN_BUY_PRICE = "upper sell price must be higher than buy price";
    public static final String NULL_ENTITY_NOT_VALID = "null entity not valid";
    public static final String ID_FIELD_IS_NULL = "Id field is null";
    public static final String ONLY_OPEN_ORDER_CAN_CHANGE_TO_CLOSE_STATE = "only OPEN order can change to CLOSE state";
    public static final String ONLY_ACTIVE_ORDER_CAN_CHANGE_TO_OPEN_STATE = "only ACTIVE order can change to OPEN state";
    public static final String ONLY_ACTIVE_ORDER_CAN_CHANGE_TO_CANCELED_STATE = "only ACTIVE order can change to CANCELED state";
    public static final String ONLY_ACTIVE_ORDER_CAN_CHANGE_TO_EXPIRED_STATE = "only ACTIVE order can change to EXPIRED state";
    public static final String ID_IS_NOT_VALID = "Id is not valid";
    public static final String ACTIVE_STATE_IS_INIT_STATE = "Active order is init state and can not transmitted from any state";
}
