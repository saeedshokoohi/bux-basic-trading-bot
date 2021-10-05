package com.bux.bot.basic_trading_bot.model;

import com.bux.bot.basic_trading_bot.model.enums.BotOrderStatus;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="bot_order_info")
public class BotOrderInfo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column
    private String title;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private BigDecimal buyPrice;

    @Column(nullable = false)
    private BigDecimal upperSellPrice;

    @Column(nullable = false)
    private BigDecimal lowerSellPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BotOrderStatus status;

    @Column
    private String positionId;

    @Column(nullable = true)
    private String description;




}
