package rsocket.learning.prices.impl;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import rsocket.learning.prices.PriceData;
import rsocket.learning.prices.PriceDataDeserializer;

@RequiredArgsConstructor
public class JsonPriceDataDeserializer implements PriceDataDeserializer {
	private final Gson gson;

	@Override
	public PriceData deserialize(byte[] payload) {
		return gson.fromJson(new String(payload, StandardCharsets.UTF_8), PriceData.class);
	}
}
