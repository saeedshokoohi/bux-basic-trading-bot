package com.bux.bot.basic_trading_bot.entity;

import com.bux.bot.basic_trading_bot.entity.enums.BotOrderStatus;

import javax.persistence.*;

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
    private Double buyPrice;

    @Column(nullable = false)
    private Double upperSellPrice;

    @Column(nullable = false)
    private Double lowerSellPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BotOrderStatus status;

    @Column
    private String positionId;

    @Column
    private String description;
    @Column(nullable = false)
    private String amount;
    @Column
    private int leverage=2;
    @Column
    private int decimals=2;

    @Column
    private long createDate;
    @Column
    private String currency="BUX";
    @Transient

    private boolean isProcessing=false;

    //todo : fetch type must be lazy
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "openPositionId", referencedColumnName = "id")
    private OrderOpenPosition openPosition;

    //todo : fetch type must be lazy
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "closePositionId", referencedColumnName = "id")
    private OrderClosePosition closePosition;



    public BotOrderInfo() {
    }

    public BotOrderInfo(String title, String productId,String amount, Double buyPrice, Double upperSellPrice, Double lowerSellPrice) {
        this.title = title;
        this.productId = productId;
        this.buyPrice = buyPrice;
        this.upperSellPrice = upperSellPrice;
        this.lowerSellPrice = lowerSellPrice;
        this.amount=amount;
    }

    public BotOrderInfo(Long id, String title, String productId,String amount, Double buyPrice, Double upperSellPrice, Double lowerSellPrice, BotOrderStatus status, String positionId, String description) {
        this.id = id;
        this.title = title;
        this.productId = productId;
        this.buyPrice = buyPrice;
        this.upperSellPrice = upperSellPrice;
        this.lowerSellPrice = lowerSellPrice;
        this.status = status;
        this.positionId = positionId;
        this.description = description;
        this.amount=amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(Double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public Double getUpperSellPrice() {
        return upperSellPrice;
    }

    public void setUpperSellPrice(Double upperSellPrice) {
        this.upperSellPrice = upperSellPrice;
    }

    public Double getLowerSellPrice() {
        return lowerSellPrice;
    }

    public void setLowerSellPrice(Double lowerSellPrice) {
        this.lowerSellPrice = lowerSellPrice;
    }

    public BotOrderStatus getStatus() {
        return status;
    }

    public void setStatus(BotOrderStatus status) {
        this.status = status;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getLeverage() {
        return leverage;
    }

    public void setLeverage(int leverage) {
        this.leverage = leverage;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public OrderOpenPosition getOpenPosition() {
        return openPosition;
    }

    public void setOpenPosition(OrderOpenPosition openPosition) {
        this.openPosition = openPosition;
    }

    public OrderClosePosition getClosePosition() {
        return closePosition;
    }

    public void setClosePosition(OrderClosePosition closePosition) {
        this.closePosition = closePosition;
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public void setProcessing(boolean processing) {
        isProcessing = processing;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
}
