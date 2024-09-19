package rsocket.learning.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.config.EnableIntegration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableIntegration
@Slf4j
@Import({
		RSocketEndpointConfig.class,
		RabbitEndpointConfig.class
})
public class TestConfig {

}
