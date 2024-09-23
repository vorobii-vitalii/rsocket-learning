package rsocket.learning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.stream.Environment;

import citrus.rabbitmq.streams.RabbitMQStreamsEndpoint;
import citrus.rabbitmq.streams.RabbitMQStreamsEndpointConfiguration;
import rsocket.learning.tests.BaseIntegrationTest;

@Configuration
public class RabbitEndpointConfig {
	public static final String VHOST = "%2f";

	@Bean
	RabbitMQStreamsEndpoint rabbitMQStreamsEndpoint() {
		int rabbitStreamsPort = BaseIntegrationTest.RABBIT.getMappedPort(5552);
		return new RabbitMQStreamsEndpoint(RabbitMQStreamsEndpointConfiguration.builder()
				.environment(Environment.builder()
						.uri("rabbitmq-stream://guest:guest@localhost:%d/".formatted(rabbitStreamsPort) + VHOST)
						.build())
				.name("prices-stream-endpoint")
				.timeout(5000L)
				.streamName(BaseIntegrationTest.PRICES_STREAM)
				.build());
	}
}
