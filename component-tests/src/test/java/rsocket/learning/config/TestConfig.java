package rsocket.learning.config;

import static rsocket.learning.tests.BaseIntegrationTest.PRICES_SERVICE;
import static rsocket.learning.tests.BaseIntegrationTest.RSOCKET_PORT;

import org.citrusframework.endpoint.Endpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.rsocket.ClientRSocketConnector;

import rsocket.learning.utils.IntegrationFlowRegistrar;
import rsocket.learning.utils.RSocketEndpointFactory;

@Configuration
@EnableIntegration
public class TestConfig {

	@Bean
	IntegrationFlowRegistrar integrationFlowRegistrar(IntegrationFlowContext integrationFlowContext) {
		return new IntegrationFlowRegistrar(integrationFlowContext);
	}

	@Bean
	public ClientRSocketConnector clientRSocketConnector() {
		ClientRSocketConnector clientRSocketConnector = new ClientRSocketConnector("localhost", PRICES_SERVICE.getMappedPort(RSOCKET_PORT));
		clientRSocketConnector.setAutoStartup(false);
		return clientRSocketConnector;
	}

	@Bean
	public Endpoint rSocketEndpoint(IntegrationFlowRegistrar flowRegistrar) {
		return new RSocketEndpointFactory(flowRegistrar).createRSocketEndpoint(clientRSocketConnector());
	}

}
