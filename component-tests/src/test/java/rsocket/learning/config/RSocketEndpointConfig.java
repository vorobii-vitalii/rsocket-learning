package rsocket.learning.config;

import static rsocket.learning.tests.BaseIntegrationTest.PRICES_SERVICE;
import static rsocket.learning.tests.BaseIntegrationTest.RSOCKET_PORT;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import citrus.rsocket.RSocketEndpoint;
import citrus.rsocket.RSocketEndpointConfiguration;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RSocketEndpointConfig {
	private static final String LOOPBACK = "127.0.0.1";

	@Bean
	RSocketEndpoint rSocketEndpoint() {
		return new RSocketEndpoint(RSocketEndpointConfiguration.builder()
				.host(LOOPBACK)
				.port(PRICES_SERVICE.getMappedPort(RSOCKET_PORT))
				.name("prices-service-endpoint")
				.timeout(2000L)
				.serverResponsesQueueName("prices-service-responses-queue")
				.build());
	}

}
