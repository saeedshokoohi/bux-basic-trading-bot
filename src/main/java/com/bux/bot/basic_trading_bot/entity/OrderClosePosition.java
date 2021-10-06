package com.bux.bot.basic_trading_bot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="order_close_position")
@Data
@NoArgsConstructor
public class OrderClosePosition extends BasePosition {
    @Column
    private String profitAndLossCurrency;
    @Column
    private int profitAndLossDecimals;
    @Column
    private String profitAndLossAmount;

}
