package com.domain.pricehandler.service;

import com.domain.pricehandler.model.InstrumentName;
import com.domain.pricehandler.model.Price;
import com.domain.pricehandler.service.messaging.PriceFeedListener;
import com.domain.pricehandler.service.messaging.PriceFeedListenerImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@Import(PriceFeedListenerImpl.class)
public class PriceFeedListenerUnitTests {

    @Autowired
    private PriceFeedListener priceFeedListener;

    @MockBean
    private PriceService priceService;

    @Test
    public void givenNoPrices_whenMessageWithOnePricePublished_UploadPrice() {
        String message = "106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001";
        BigDecimal priceFeedBid = new BigDecimal(1.1000);
        BigDecimal priceFeedAsk = new BigDecimal(1.2000);

        BigDecimal priceBid = priceFeedBid.multiply(new BigDecimal(1-0.001));
        BigDecimal priceAsk = priceFeedAsk.multiply(new BigDecimal(1+0.001));

        List<Price> prices = new ArrayList<>();
        Price price = new Price();
        price.setId(106);
        price.setInstrumentName(InstrumentName.EUR_USD);
        price.setBid(priceBid);
        price.setAsk(priceAsk);
        price.setTimestamp("01-06-2020 12:01:01:001");
        prices.add(price);

        Mockito.when(priceService.calculatePriceBid(Mockito.any(BigDecimal.class)))
                .thenReturn(priceBid);
        Mockito.when(priceService.calculatePriceAsk(Mockito.any(BigDecimal.class)))
                .thenReturn(priceAsk);

        doNothing().when(priceService).updatePrices(prices);

        priceFeedListener.onMessage(message);

        Mockito.verify(priceService, times(1)).updatePrices(prices);;
    }

    @Test
    public void givenNoPrices_whenMessageWithManyPricesPublished_UploadPrices() {
        String message = String.join(PriceFeedListenerImpl.newLine,
                "106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001",
                "107, EUR/JPY, 119.60,119.90,01-06-2020 12:01:02:002");

        BigDecimal priceFeedBid = new BigDecimal(1.1000);
        BigDecimal priceFeedAsk = new BigDecimal(1.2000);

        BigDecimal priceBid = priceFeedBid.multiply(new BigDecimal(1-0.001));
        BigDecimal priceAsk = priceFeedAsk.multiply(new BigDecimal(1+0.001));

        List<Price> prices = new ArrayList<>();
        Price price = new Price();
        price.setId(106);
        price.setInstrumentName(InstrumentName.EUR_USD);
        price.setBid(priceBid);
        price.setAsk(priceAsk);
        price.setTimestamp("01-06-2020 12:01:01:001");

        Price priceTwo = new Price();
        priceTwo.setId(107);
        priceTwo.setInstrumentName(InstrumentName.EUR_JPY);
        priceTwo.setBid(priceBid);
        priceTwo.setAsk(priceAsk);
        priceTwo.setTimestamp("01-06-2020 12:01:02:002");

        prices.add(price);
        prices.add(priceTwo);

        Mockito.when(priceService.calculatePriceBid(Mockito.any(BigDecimal.class)))
                .thenReturn(priceBid);
        Mockito.when(priceService.calculatePriceAsk(Mockito.any(BigDecimal.class)))
                .thenReturn(priceAsk);
        doNothing().when(priceService).updatePrices(prices);

        priceFeedListener.onMessage(message);

        Mockito.verify(priceService, times(1)).updatePrices(prices);;
    }

    @Test
    public void givenNoPrices_whenMessageWithCorruptPricePublished_ThrowException() {
        String message = "106, EUR/USD, noPrice,1.2000,01-06-2020 12:01:01:001";

        Exception exception = assertThrows(RuntimeException.class, () -> {
            priceFeedListener.onMessage(message);
        });

        String expectedExceptionMessage = "Message convertion failed due to incorrect message Format";
        String exceptionMessage = exception.getMessage();

        assertTrue(exceptionMessage.contains(expectedExceptionMessage));
    }
}