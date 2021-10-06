package com.bux.bot.basic_trading_bot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="order_open_position")
@Data
@NoArgsConstructor
public class OrderOpenPosition extends BasePosition{


}
