package rsocket.learning.tests;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.MountableFile;

public class BaseIntegrationTest {
	public static final int RSOCKET_PORT = 7878;
	public static final Network NETWORK = Network.newNetwork();
	public static final String PRICES_STREAM = "prices_stream";
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseIntegrationTest.class);
	public static final RabbitMQContainer RABBIT = new RabbitMQContainer("rabbitmq:3.13-rc-management")
			.withNetwork(NETWORK)
			.withNetworkAliases("rabbitmq")
			.withExposedPorts(15672, 5552)
			.withEnv(Map.of(
//					"RABBITMQ_DEFAULT_USER", "rabbit",
//					"RABBITMQ_DEFAULT_PASS", "rabbit",
//					"RABBITMQ_SERVER_ADDITIONAL_ERL_ARGS", "-rabbitmq_stream advertised_host localhost"
			))
			.withQueue(PRICES_STREAM, false, true, Map.of("x-queue-type", "stream"))
			.withCopyFileToContainer(MountableFile.forClasspathResource("enabled_plugins"), "/etc/rabbitmq/enabled_plugins")
			.withLogConsumer(new Slf4jLogConsumer(LOGGER).withPrefix("rabbitmq"));
	public static final GenericContainer<?> PRICES_SERVICE = new GenericContainer<>(
			new ImageFromDockerfile()
					.withFileFromPath("/app.jar", Paths.get(MountableFile.forHostPath("../rsocket-server/build/libs/app.jar").getResolvedPath()))
					.withFileFromPath("/Dockerfile", Paths.get(MountableFile.forHostPath("../Dockerfile").getResolvedPath())))
			.withNetwork(NETWORK)
			.dependsOn(RABBIT)
			.waitingFor(Wait.forLogMessage(".*Starting server.*", 1))
			.withStartupTimeout(Duration.ofSeconds(5))
			.withStartupAttempts(10)
			.withEnv(Map.of("RABBITMQ_URI", "rabbitmq-stream://guest:guest@rabbitmq:5552/%2f"))
			.withExposedPorts(RSOCKET_PORT)
			.withLogConsumer(new Slf4jLogConsumer(LOGGER).withPrefix("prices-service"));

	static {
		RABBIT.start();
		PRICES_SERVICE.start();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			RABBIT.stop();
			PRICES_SERVICE.stop();
			NETWORK.close();
		}));
	}

}
