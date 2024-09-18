package rsocket.learning.dto;

import lombok.Builder;

@Builder
public record PricesStreamRequest(String symbol) {
}
