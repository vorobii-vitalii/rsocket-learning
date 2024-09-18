## Objective of this project is to learn RSocket protocol

### Project idea - price streaming server
### Technologies used so far - Java 22, RSocket, Gson, Citrus Framework, Dagger, RabbitMQ Streams

### Price streaming server subscribes to RMQ Stream which produces stock prices in realtime in following format
```json

{
 "symbol": "ABBN",
 "askPrice": 2.5,
 "bidPrice": 4.0,
  "timestamp": "16::Sep::2024 20::30::45"
}
```
### Also, it accepts RSocket connections and allows clients to subscribe to price updates for certain stock (ABBN, APPL, TSL etc) 