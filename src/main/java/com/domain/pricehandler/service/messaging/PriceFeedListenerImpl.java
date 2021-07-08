package com.domain.pricehandler.service.messaging;

import com.domain.pricehandler.model.InstrumentName;
import com.domain.pricehandler.model.Price;
import com.domain.pricehandler.service.PriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("priceFeedReceiver")
@Slf4j
public class PriceFeedListenerImpl implements PriceFeedListener {

    @Autowired
    PriceService priceService;

    public static final String newLine = System.getProperty("line.separator");

    public void onMessage(String message) {
        String[] lines = message.split(newLine);
        List<Price> prices = Arrays
                .stream(lines)
                .map(l -> {
                    try {
                        return convertMessageToPrice(l);
                    } catch (Exception e) {
                        throw new RuntimeException("Message convertion failed due to incorrect message Format");
                    }
                })
                .collect(Collectors.toList());
        priceService.updatePrices(prices);
    }

    /**
     * Splits message and builds Price object
     * @param line
     * @return
     * @throws Exception - this should be amended to specific exception
     */
    private Price convertMessageToPrice(String line) throws Exception {
        String[] fields = line.trim().split("\\s*,\\s*");

        Price price = new Price();

        price.setId(Integer.valueOf(fields[0]));

        Optional<InstrumentName> opt = InstrumentName.valueOfLabel(fields[1]);
        opt.ifPresent(instrument -> price.setInstrumentName(instrument));

        price.setBid(priceService.calculatePriceBid(new BigDecimal(fields[2])));
        price.setAsk(priceService.calculatePriceAsk(new BigDecimal(fields[3])));

        price.setTimestamp(fields[4]);

        return price;
    }
}
