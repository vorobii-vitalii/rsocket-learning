package rsocket.learning.tests;

import org.citrusframework.annotations.CitrusEndpoint;
import org.citrusframework.config.CitrusSpringConfig;
import org.citrusframework.endpoint.Endpoint;
import org.citrusframework.junit.jupiter.spring.CitrusSpringSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;

@Testcontainers
@CitrusSpringSupport
@ContextConfiguration(classes = CitrusSpringConfig.class)
@Slf4j
public class IntegrationTest extends BaseIntegrationTest {

	@CitrusEndpoint(name = "rSocketEndpoint")
	Endpoint rSocketEndpoint;

	@Test
	void test() {
		log.info("endpoint = {}", rSocketEndpoint);
		Assertions.assertTrue(true);
	}

}
