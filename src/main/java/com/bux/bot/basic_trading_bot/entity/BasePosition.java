package com.bux.bot.basic_trading_bot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@MappedSuperclass
@NoArgsConstructor
public class BasePosition {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column
    public Long orderId;
    @Column
    public String clientId;
    @Column
    public String positionId;
    @Column
    public String productSecurityId;
    @Column
    public String productSymbol;
    @Column
    public String productDisplayName;
    @Column
    public String investingAmount;
    @Column
    private String investingAmountCurrency;
    @Column
    private int investingAmountDecimals;
    @Column
    public String priceCurrency;
    @Column
    public int priceDecimals;
    @Column
    public String priceAmount;
    @Column
    public int leverage;
    @Column
    public String direction;
    @Column
    public String type;
    @Column
    public long dateCreated;

}
