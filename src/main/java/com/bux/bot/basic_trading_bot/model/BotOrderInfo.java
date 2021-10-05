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

    @Column(nullable = true)
    private String description;

    public BotOrderInfo() {
    }

    public BotOrderInfo(String title, String productId, Double buyPrice, Double upperSellPrice, Double lowerSellPrice) {
        this.title = title;
        this.productId = productId;
        this.buyPrice = buyPrice;
        this.upperSellPrice = upperSellPrice;
        this.lowerSellPrice = lowerSellPrice;
    }

    public BotOrderInfo(Long id, String title, String productId, Double buyPrice, Double upperSellPrice, Double lowerSellPrice, BotOrderStatus status, String positionId, String description) {
        this.id = id;
        this.title = title;
        this.productId = productId;
        this.buyPrice = buyPrice;
        this.upperSellPrice = upperSellPrice;
        this.lowerSellPrice = lowerSellPrice;
        this.status = status;
        this.positionId = positionId;
        this.description = description;
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
}
