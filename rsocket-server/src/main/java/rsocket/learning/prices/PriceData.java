package rsocket.learning.prices;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record PriceData(String symbol, BigDecimal askPrice, BigDecimal bidPrice, LocalDateTime timestamp) {
}
