package com.domain.pricehandler;

import com.domain.pricehandler.service.messaging.PriceFeedListener;
import com.domain.pricehandler.service.messaging.PriceFeedListenerImpl;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
@Slf4j
public class PriceHandlerApplication {

	public static void main(String[] args) throws IOException {
		ConfigurableApplicationContext context = SpringApplication.run(PriceHandlerApplication.class, args);

		// For testing purposes
		log.info("Publishing sample price feed: START");
		try {
			String message1 = String.join(PriceFeedListenerImpl.newLine,
					"106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001",
					"107, EUR/JPY, 119.60,119.90,01-06-2020 12:01:02:002",
					"108, GBP/USD, 1.2500,1.2560,01-06-2020 12:01:02:002");

			String message2 = String.join(PriceFeedListenerImpl.newLine,
					"109, GBP/USD, 1.2499,1.2561,01-06-2020 12:01:02:100",
					"110, EUR/JPY, 119.61,119.91,01-06-2020 12:01:02:110");

			PriceFeedListener priceFeedListener = (PriceFeedListener) context.getBean("priceFeedReceiver");
			priceFeedListener.onMessage(message1);
			priceFeedListener.onMessage(message2);
		} catch( RuntimeException e) {
			/** In case of any price feed coming in incorrect format, we should stop the service
			 * as we should never show any incorrect price. Prices are crucial business information for a bank.
			 */
			throw new IOException("Price Feed Listener failure, shutting down");
		}
		log.info("Publishing sample price feed: END");
	}

	// Used in Controller for mapping from internal Price to external PriceDto
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
