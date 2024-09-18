package rsocket.learning.config;

import static rsocket.learning.tests.BaseIntegrationTest.PRICES_SERVICE;
import static rsocket.learning.tests.BaseIntegrationTest.RSOCKET_PORT;

import org.citrusframework.channel.ChannelEndpointBuilder;
import org.citrusframework.endpoint.Endpoint;
import org.citrusframework.endpoint.direct.DirectEndpointBuilder;
import org.citrusframework.message.DefaultMessage;
import org.citrusframework.message.DefaultMessageQueue;
import org.citrusframework.message.MessageQueue;
import org.reactivestreams.Subscription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.rsocket.ClientRSocketConnector;
import org.springframework.integration.rsocket.RSocketInteractionModel;
import org.springframework.integration.rsocket.outbound.RSocketOutboundGateway;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;

@Configuration
@Slf4j
public class RSocketEndpointConfig {

	@Bean
	public ClientRSocketConnector clientRSocketConnector() {
		ClientRSocketConnector clientRSocketConnector = new ClientRSocketConnector("127.0.0.1", PRICES_SERVICE.getMappedPort(RSOCKET_PORT));
		clientRSocketConnector.setAutoStartup(false);
		return clientRSocketConnector;
	}

	@Bean
	MessageQueue responsesQueue() {
		return new DefaultMessageQueue("responses-queue");
	}

	@Bean
	public Endpoint rSocketRequestEndpoint() {
		return new ChannelEndpointBuilder().channel(requestChannel()).build();
	}

	@Bean
	public Endpoint rSocketResponseEndpoint(MessageQueue responsesQueue) {
		return new DirectEndpointBuilder().queue(responsesQueue).build();
	}

	@Bean
	public MessageChannel requestChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageChannel responseChannel() {
		return new DirectChannel();
	}

	@ServiceActivator(inputChannel = "requestChannel")
	@Bean
	public RSocketOutboundGateway rsocketOutboundGateway() {
		RSocketOutboundGateway rsocketOutboundGateway = new RSocketOutboundGateway("/");
		rsocketOutboundGateway.setInteractionModel(RSocketInteractionModel.requestStream);
		rsocketOutboundGateway.setLoggingEnabled(true);
		rsocketOutboundGateway.setClientRSocketConnector(clientRSocketConnector());
		rsocketOutboundGateway.setOutputChannel(responseChannel());
		return rsocketOutboundGateway;
	}

	@Bean
	@ServiceActivator(inputChannel = "responseChannel")
	public MessageHandler responseChannelHandler(MessageQueue responsesQueue) {
		return message -> {
			log.info("Response = {}", message);
			@SuppressWarnings("unchecked")
			Flux<String> fluxMap = (Flux<String>) message.getPayload();
			fluxMap.subscribe(new CoreSubscriber<>() {
				@Override
				public void onSubscribe(Subscription subscription) {
					subscription.request(Long.MAX_VALUE);
				}

				@Override
				public void onNext(String v) {
					log.info("Adding {} to queue", v);
					responsesQueue.send(new DefaultMessage(v));
				}

				@Override
				public void onError(Throwable throwable) {
					log.error("Error", throwable);
				}

				@Override
				public void onComplete() {

				}
			});
		};
	}



}
