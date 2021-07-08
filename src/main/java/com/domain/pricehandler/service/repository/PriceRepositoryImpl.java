package com.domain.pricehandler.service.repository;

import com.domain.pricehandler.model.InstrumentName;
import com.domain.pricehandler.model.Price;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This repository does not need to be persisted, hence @Component annotation was used
 */
@Component
@Slf4j
public class PriceRepositoryImpl implements PriceRepository {

    // Have chosen hashmap for quick updates
    private Map<InstrumentName, Price> prices;

    public PriceRepositoryImpl() {
        prices = new HashMap<>();
    }

    public Optional<Price> getByInstrumentName(InstrumentName instrumentName) {
        Price price = prices.get(instrumentName);
        Optional<Price> opt = Optional.ofNullable(price);
        return opt;
    }

    public Optional<List<Price>> get() {
        List<Price> pricesAsList = prices.values().stream()
                .collect(Collectors.toList());
        return Optional.ofNullable(pricesAsList);
    }

    public void update(Price price) {
        prices.put(price.getInstrumentName(), price);
    }

    public void clear() {
        prices.clear();
    }
}

