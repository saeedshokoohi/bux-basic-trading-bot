##  BUX -  Basic Trading Bot
This project is a basic trading bot that is implemented for the BUX assignment.


##  Features
- Obtaining and retaining predefined trade information.
- Connecting to the BUX server via WebSockets to track product price changes.
- Execute a buy-order through the REST API service when the price is equal to or lower than the defined buy-price.
- Monitoring the open trading position product price to achieve the defined upper and lower sell price.
- Run a sell-order through the REST API service when the price is equal to or higher than the defined upper-sell-price (take-profit) or equal to or lower than the defined lower-sell-price (stop-loss).


# Design

## State Diagram


TradeInfo entity is the main entity in the project which has following life-cycle:

- **Active**: Once the add tradeInfo service is called, a new tradeInfo entity will be created, and its initial state is active.

- **Open** : The trade state changes to Open when the product price hits the defined buy price and the buy action is successful.

- **Closed** (end-state): After the product price hits the upper sell price or lower sell price and the sell action is successful, the trade state changes to Closed.

- **Cancelled** (end-state): The user can cancel the active trade manually. In this case, the trade state changes to canceled, so the price monitoring is stopped for the canceled state trades.

- **Expired** (end-state) : If the user enters an expiry date for a trade, then the active trade will turn into an expired trade; the current trade is no longer monitored by the price monitoring system.

In the diagram below, the following states and transitions are illustrated:



![Trade State Diagram](https://github.com/saeedshokoohi/bux-basic-trading-bot/blob/75e017ed0edab8893878d3f2552b9a7c2d48ab4e/design/images/Trading%20State%20Diagram.jpg)


## Entity Relationship Diagram
One main entity in the project data model is "*BotOrderInfo*," while the other two detail entities are "*OrderOpenPosition*" and "*OrderClosePosition*.".

- **BotOrderInfo**: contains all of the information needed by the bot engine to decide whether to open and close a position.

- **OrdOpenPosition**: Upon opening a position, the position details are recorded in the following entity.

- **OrderClosePosition**: The information about a closed position is stored in the entity OrderClosePosition.


The following diagram illustrates the fields and relations of the entities:

![Entity Relationship Diagram](https://github.com/saeedshokoohi/bux-basic-trading-bot/blob/67eeb7ea4ee087283210d928bef5967b82a5ff4b/design/images/entity_relationship_diagram.png)

## Implementation Architecture

**RESTful API:** Allows end-users to add, modify, and retrieve BotOrderInfo data through a RESTful API endpoint.

**BotOrderService:** This package manages the storage and retrieval of data from the database. Also, when any action is taken against the current state, send the event to the global bus.

**BotEngineService:** Retrieves candidate bot orders and listens to the price stream from the TrackerService. After checking the bot trade logic, the bot decides whether to open a position, close a position, or just wait for the proper buy price.

**ClientServices:** The client service package contains two main services: one for tracking the price and another for trading. The TrackerService tracks the price and the TraderService performs trades. These two services are abstract and may have multiple implementations mapped to multiple brokers. 

**EventBus:** As its name suggests, this package handles events from various layers that facilitate loose coupling between services. To ensure ease of implementation and separation of responsibilities, the WebSocket event bus is considered separately.

The following diagram illustrates the implementation layers and their communications :


![Implementation Architecture Diagram](https://github.com/saeedshokoohi/bux-basic-trading-bot/blob/13e00cc71d737e102b563e9612b8b10acc9f5813/design/images/implementation_architecture_diagram.png)


# Implemetation

## Project Structure

The project is a standalone spring-boot application, with the following five main packages in the source project:

![Project Structure Diagram](https://github.com/saeedshokoohi/bux-basic-trading-bot/blob/429e9ece4947c2410dd813b24263ed32b1653ed6/design/images/project_structure_diagram.png)

- **web**: Developed classes for managing web resources. 
The *RestController* has been used to implement the RESTAPI, whose methods return generic types ResponseEntity for additional data processing beyond the basic data. If a request contains a bad validation error, detailed data, including EntityName and target fields, will be returned as a validation error.

- **service**: Integrated business logic service classes.
For the purpose of providing more performance and addressing real-time issues, reactive programming is seen as the main foundation. Consequently, most methods return Mono and Flux as return types. Application logic for validation and trading are also integrated into this layer. 
- **repository**: Including classes for data accessing the repository.
The repository service simply uses spring data *CrudRepository*, for handling current features. Additionally, if a need arises, the upper layer can easily be integrated with the reactive repository which is not implemented in the current state.

- **client**: implemented communications with third-party web servers. This includes rest-based communications and WebSocket services.
There are two main sections in the client layer. The first is for calling the broker's RESTAPI for performing trades using *WebClient*. The second is for connecting to the websocket server to track price changes using *ReactorNettyWebSocketClient* implementation.
TraderService and TrackerService are two interfaces implemented in every section. As a consequence, we can modify or replace implementations without affecting other parts of the code.

- **event**: Class that handles application-level events.
We have a GlobalEventBus that transmits generic GlobalEvents among the application layers. WebSocketEventBus is responsible for handling events related to WebSockets, which eventually emit events to GlobalEventBus to ensure integrity.

- **dto**: Data Transfer Object classes that are used to transfer data between web and client-side applications.
DTO classes are used for inter-class communications, as well as passing and receiving data to and from third parties from the application. Boilerplate codes are omitted from constructors and accessors via Lombok library. 

- **exception**: consists of several types of exceptions.
Exceptions of all kinds are gathered in this package to throw meaningful exceptions. Additionally, EntityValidationExceptions extend exceptions by adding more specified data for error handling at any level of validation.

- **config**: Contains classes that hold configuration-related objects.
Structured configuration classes that are mapped to *.yml application properties.
During configuration, we provide a special configuration parameter called *BrokerConfiguration*, which supports multiple broker configurations. It is further separated into *WebSocket* configuration and *Rest* configuration.


