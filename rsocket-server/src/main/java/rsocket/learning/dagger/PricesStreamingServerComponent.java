package rsocket.learning.dagger;

import dagger.Component;
import io.rsocket.SocketAcceptor;
import jakarta.inject.Singleton;
import rsocket.learning.dagger.modules.PricesStreamingServerModule;

@Singleton
@Component(modules = PricesStreamingServerModule.class)
public interface PricesStreamingServerComponent {
	SocketAcceptor createSocketAcceptor();
}
