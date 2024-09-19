package rsocket.learning;

import java.util.Objects;

import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.slf4j.Slf4j;
import rsocket.learning.dagger.DaggerPricesStreamingServerComponent;

@Slf4j
public class PricesStreamingServer {
	private static final int SERVER_PORT = 7878;

	public static void main(String[] args) {
		try {
			SocketAcceptor socketAcceptor = DaggerPricesStreamingServerComponent.create().createSocketAcceptor();
			log.info("Starting server");
			Objects.requireNonNull(
							RSocketServer.create(socketAcceptor)
									.payloadDecoder(PayloadDecoder.ZERO_COPY)
									.bind(TcpServerTransport.create(SERVER_PORT))
									.block())
					.onClose()
					.block();
		}
		catch (Exception e) {
			log.error("Error", e);
			System.exit(1);
		}
	}

}
