package rsocket.learning.prices;

public interface PriceDataDeserializer {
	PriceData deserialize(byte[] payload);
}
