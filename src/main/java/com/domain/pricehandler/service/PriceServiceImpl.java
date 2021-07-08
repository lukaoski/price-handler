package com.domain.pricehandler.service;

import com.domain.pricehandler.model.InstrumentName;
import com.domain.pricehandler.model.Price;
import com.domain.pricehandler.service.repository.PriceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@ConfigurationProperties(prefix = "commission")
@Slf4j
public class PriceServiceImpl implements PriceService {

    // Value is read from properties config
    Double bidMargin;

    // Value is read from properties config
    Double askMargin;

    @Autowired
    private PriceRepository priceRepository;

    /**
     * Returning stored prices for given instrument
     * @param instrumentNameAsString
     * @return
     */
    @Override
    public Mono<Price> getPrice(String instrumentNameAsString) {
        Optional<InstrumentName> opt = InstrumentName.valueOfLabel(instrumentNameAsString);
        return opt.map(instrumentName -> {
            Optional<Price> optPrice = priceRepository.getByInstrumentName(instrumentName);
            return optPrice.map(price -> Mono.just(price))
                    .orElseGet(() -> Mono.empty());
        }).orElseGet(() -> {
            // Logging added in expected rare test cases only to minimize effect on service responsiveness
            log.warn("Unsupported instrument name requested: ignoring request");
            return Mono.empty();
        });
    }

    /**
     * Returning all stored prices
     * @return
     */
    @Override
    public Flux<Price> getPrices() {
        Optional<List<Price>> opt = priceRepository.get();
        return opt.map(list -> Flux.fromIterable(list))
                .orElseGet(() -> Flux.empty());
    }

    // This method could be moved to utils (not created due to time constraints)
    private static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     *  Algorithm used to update prices:
     *  1. Filter by supported instrument name
     *  2. Sort prices by id descending
     *  3. Filter unique by instrument name (checking starting with highest id)
     * @param prices
     */
    @Override
    public void updatePrices(List<Price> prices) {
        List<Price> sortedPrices = prices.stream()
                .filter(p -> InstrumentName.contains(p.getInstrumentName()))
                .sorted(Comparator.comparingInt(Price::getId))
                .filter(distinctByKey(p -> p.getInstrumentName()))
                .collect(Collectors.toList());

        for(Price p: sortedPrices) {
            Optional<Price> currentPrice = priceRepository.getByInstrumentName(p.getInstrumentName());
            if(currentPrice.isEmpty() || currentPrice.get().getId() < p.getId()) {
                priceRepository.update(p);
            }
        }
    }

    public void clearPrices() {
        priceRepository.clear();
    }

    public void setBidMargin(final double bidMargin) {
        this.bidMargin = bidMargin;
    }

    public void setAskMargin(final double askMargin) {
        this.askMargin = askMargin;
    }

    @Override
    public BigDecimal calculatePriceBid(BigDecimal priceFeedBid) {
        return priceFeedBid.multiply(new BigDecimal(1+bidMargin));
    }

    @Override
    public BigDecimal calculatePriceAsk(BigDecimal priceFeedAsk) {
        return priceFeedAsk.multiply(new BigDecimal(1+askMargin));
    }
}
