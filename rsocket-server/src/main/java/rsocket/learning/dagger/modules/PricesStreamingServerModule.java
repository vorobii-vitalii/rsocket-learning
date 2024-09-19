package rsocket.learning.dagger.modules;

import dagger.Module;
import dagger.Provides;
import io.rsocket.SocketAcceptor;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rsocket.learning.prices.PriceData;
import rsocket.learning.prices.impl.StockPriceReaderImpl;
import rsocket.learning.serialization.PayloadDataDeserializer;
import rsocket.learning.serialization.PayloadDataSerializer;
import rsocket.learning.socket.PricesStreamingRSocket;

@Module(includes = {
		SerializationModule.class,
		RabbitModule.class
})
public class PricesStreamingServerModule {

	@Singleton
	@Provides
	SocketAcceptor socketAcceptor(
			Flux<PriceData> priceDataStream,
			PayloadDataSerializer payloadDataSerializer,
			PayloadDataDeserializer payloadDataDeserializer
	) {
		return (setup, sendingSocket) -> {
			var stockPriceReader = new StockPriceReaderImpl(priceDataStream);
			return Mono.just(new PricesStreamingRSocket(payloadDataDeserializer, payloadDataSerializer, stockPriceReader));
		};
	}

}
