package citrus.rsocket;

import org.citrusframework.message.DefaultMessage;
import org.citrusframework.message.Message;

public class RSocketRequest extends DefaultMessage {
	private static final String REQUEST_TYPE = "Request-Type";

	public RSocketRequest(Message message) {
		this(message.getPayload(String.class), RSocketRequestType.valueOf(message.getHeader(REQUEST_TYPE).toString()));
	}

	public RSocketRequest(String requestPayload, RSocketRequestType requestType) {
		super(requestPayload);
		setHeader(REQUEST_TYPE, requestType);
	}

	public RSocketRequestType getRequestType() {
		return (RSocketRequestType) getHeader(REQUEST_TYPE);
	}

}
