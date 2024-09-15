package rsocket.learning.socket;

import io.rsocket.RSocket;

public interface RSocketFactory {
	RSocket create();
}
