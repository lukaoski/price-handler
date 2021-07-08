package com.domain.pricehandler.service;

import com.domain.pricehandler.model.Price;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service Interface for storing and retrieving prices
 */
public interface PriceService {

    public Mono<Price> getPrice(String instrumentName);

    public Flux<Price> getPrices();

    public void updatePrices(List<Price> prices);

    public void clearPrices();

    public BigDecimal calculatePriceBid(BigDecimal priceFeedBid);

    public BigDecimal calculatePriceAsk(BigDecimal priceFeedAsk);
}
