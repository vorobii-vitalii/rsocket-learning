package citrus.rsocket;

import org.citrusframework.endpoint.EndpointConfiguration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RSocketEndpointConfiguration implements EndpointConfiguration {
	private long timeout;
	private String name;
	private String host;
	private int port;
	private String serverResponsesQueueName;
}
