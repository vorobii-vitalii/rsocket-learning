package rsocket.learning.utils;

import org.citrusframework.channel.ChannelEndpointBuilder;
import org.citrusframework.endpoint.Endpoint;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.rsocket.ClientRSocketConnector;
import org.springframework.integration.rsocket.RSocketInteractionModel;
import org.springframework.integration.rsocket.dsl.RSockets;

import lombok.RequiredArgsConstructor;
import rsocket.learning.dto.PriceUpdate;

@RequiredArgsConstructor
public class RSocketEndpointFactory {
	private final IntegrationFlowRegistrar integrationFlowRegistrar;

	public Endpoint createRSocketEndpoint(ClientRSocketConnector rSocketConnector) {
		var channel = new DirectChannel();
		var integrationFlow = IntegrationFlow.from(channel)
				.log()
				.handle(RSockets.outboundGateway("/")
						.interactionModel(RSocketInteractionModel.requestStream)
						.clientRSocketConnector(rSocketConnector)
						.expectedResponseType(PriceUpdate.class))
				.handle(channel)
				.get();
		integrationFlowRegistrar.register(integrationFlow);
		return new ChannelEndpointBuilder().channel(channel).build();
	}

}
