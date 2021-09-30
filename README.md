##  BUX -  Basic Trading Bot
This project is a basic trading bot that is implemented for the BUX assignment.


##  Features
- Obtaining and retaining predefined trade information.
- Connecting to the BUX server via WebSockets to track product price changes.
- Execute a buy-order through the REST API service when the price is equal to or lower than the defined buy-price.
- Monitoring the open trading position product price to achieve the defined upper and lower sell price.
- Run a sell-order through the REST API service when the price is equal to or higher than the defined upper-sell-price (take-profit) or equal to or lower than the defined lower-sell-price (stop-loss).



## State Diagram


TradeInfo entity is the main entity in the project which has following life-cycle:

- **Active State**: Once the add tradeInfo service is called, a new tradeInfo entity will be created, and its initial state is active.

- **Open State** : The trade state changes to Open when the product price hits the defined buy price and the buy action is successful.

- **Close State** (end-state): After the product price hits the upper sell price or lower sell price and the sell action is successful, the trade state changes to Closed.

- **Cancelled State** (end-state): The user can cancel the active trade manually. In this case, the trade state changes to canceled, so the price monitoring is stopped for the canceled state trades.

- **Expired State** (end-state) : If the user enters an expiry date for a trade, then the active trade will turn into an expired trade; the current trade is no longer monitored by the price monitoring system.

In the diagram below, the following states and transitions are illustrated:



![Trade State Diagram](https://github.com/saeedshokoohi/bux-basic-trading-bot/blob/75e017ed0edab8893878d3f2552b9a7c2d48ab4e/design/images/Trading%20State%20Diagram.jpg)
