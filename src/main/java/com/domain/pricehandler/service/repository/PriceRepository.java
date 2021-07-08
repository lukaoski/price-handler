package com.domain.pricehandler.service.repository;

import com.domain.pricehandler.model.InstrumentName;
import com.domain.pricehandler.model.Price;

import java.util.List;
import java.util.Optional;

/**
 * Repository Interface in which we are storing the newest prices
 */
public interface PriceRepository {

    /**
     * Retrieves price for given instrument
     * In case of more reactive approach needed, this could return Optional<Mono<Price>>
     * @param instrumentName
     * @return
     */
    public Optional<Price> getByInstrumentName(InstrumentName instrumentName);

    /**
     * Retrieves all provided prices
     * In case of more reactive approach needed, this could return Optional<Flux<Price>>
     * @return
     */
    public Optional<List<Price>> get();

    /**
     * Updates price
     * @param price
     */
    public void update(Price price);

    /**
     * Clears prices (used in testing)
     */
    public void clear();
}

