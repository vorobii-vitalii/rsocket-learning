package rsocket.learning.config;

import org.citrusframework.channel.ChannelEndpointBuilder;
import org.citrusframework.endpoint.Endpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.RabbitStream;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

import com.rabbitmq.stream.Environment;

import rsocket.learning.tests.BaseIntegrationTest;

@Configuration
public class RabbitEndpointConfig {
	public static final String VHOST = "%2f";

	@Bean
	public MessageChannel rabbitWritesChannel() {
		return new DirectChannel();
	}

	@Bean
	Environment environment() {
		int rabbitStreamsPort = BaseIntegrationTest.RABBIT.getMappedPort(5552);
		return Environment.builder().uri("rabbitmq-stream://guest:guest@localhost:%d/".formatted(rabbitStreamsPort) + VHOST).build();
	}

	@Bean
	public IntegrationFlow amqpOutbound(Environment environment) {
		return IntegrationFlow.from(rabbitWritesChannel())
				.handle(RabbitStream.outboundStreamAdapter(environment, BaseIntegrationTest.PRICES_STREAM))
				.get();
	}

	@Bean
	public Endpoint pricesStreamWriteEndpoint() {
		return new ChannelEndpointBuilder().channel(rabbitWritesChannel()).build();
	}

}
