package rsocket.learning.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record PriceUpdate(BigDecimal askPrice, BigDecimal bidPrice, LocalDateTime timestamp) {
}
