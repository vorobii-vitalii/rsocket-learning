package citrus.rabbitmq.streams;

import org.citrusframework.endpoint.EndpointConfiguration;

import com.rabbitmq.stream.Environment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RabbitMQStreamsEndpointConfiguration implements EndpointConfiguration {
	private long timeout;
	private String name;
	private Environment environment;
	private String streamName;
}
