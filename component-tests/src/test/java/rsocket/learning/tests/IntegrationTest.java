package rsocket.learning.tests;

import static org.citrusframework.actions.ReceiveMessageAction.Builder.receive;
import static org.citrusframework.actions.SendMessageAction.Builder.send;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.citrusframework.TestActionRunner;
import org.citrusframework.annotations.CitrusEndpoint;
import org.citrusframework.annotations.CitrusResource;
import org.citrusframework.config.CitrusSpringConfig;
import org.citrusframework.endpoint.Endpoint;
import org.citrusframework.junit.jupiter.spring.CitrusSpringSupport;
import org.citrusframework.message.DefaultMessage;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;

@Testcontainers
@CitrusSpringSupport
@ContextConfiguration(classes = CitrusSpringConfig.class)
@Slf4j
public class IntegrationTest extends BaseIntegrationTest {
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

	@CitrusEndpoint(name = "rSocketRequestEndpoint")
	Endpoint rSocketRequestEndpoint;

	@CitrusEndpoint(name = "rSocketResponseEndpoint")
	Endpoint rSocketResponseEndpoint;

	@CitrusEndpoint(name = "pricesStreamWriteEndpoint")
	Endpoint pricesStreamWriteEndpoint;

	@Test
	void test(@CitrusResource TestActionRunner actions) {
		log.info("endpoint = {}", rSocketRequestEndpoint);

		actions.$(send(rSocketRequestEndpoint).message(new DefaultMessage().setPayload("{\"symbol\": \"ABBN\"}")));

		var timestamp1 = DATE_TIME_FORMATTER.format(LocalDateTime.now());
		var timestamp2 = DATE_TIME_FORMATTER.format(LocalDateTime.now());
		var timestamp3 = DATE_TIME_FORMATTER.format(LocalDateTime.now());

		actions.$(send(pricesStreamWriteEndpoint).message(new DefaultMessage().setPayload("""
				{
					"symbol": "XBT/USD",
					"askPrice": 900000,
					"bidPrice": 900000,
					"timestamp": "%s"
				}
				""".formatted(timestamp1))));
		actions.$(send(pricesStreamWriteEndpoint).message(new DefaultMessage().setPayload("""
				{
					"symbol": "ABBN",
					"askPrice": 100.0,
					"bidPrice": 120.0,
					"timestamp": "%s"
				}
				""".formatted(timestamp2))));
		actions.$(send(pricesStreamWriteEndpoint).message(new DefaultMessage().setPayload("""
				{
					"symbol": "ABBN",
					"askPrice": 102.0,
					"bidPrice": 120.0,
					"timestamp": "%s"
				}
				""".formatted(timestamp3))));

		actions.$(receive(rSocketResponseEndpoint)
				.message(new DefaultMessage().setPayload("""
						{
							"askPrice": 100.0,
							"bidPrice": 120.0,
							"timestamp": "%s"
						}
						""".formatted(timestamp2))));
		actions.$(receive(rSocketResponseEndpoint)
				.message(new DefaultMessage().setPayload("""
						{
							"askPrice": 102.0,
							"bidPrice": 120.0,
							"timestamp": "%s"
						}
						""".formatted(timestamp3))));
	}

}
