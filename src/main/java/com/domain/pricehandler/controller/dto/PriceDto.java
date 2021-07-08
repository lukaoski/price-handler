package com.domain.pricehandler.controller.dto;

import com.domain.pricehandler.model.InstrumentName;
import lombok.Data;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Object returned by endpoint
 *
 * Assumption was made that, apart for instrument name, only values of bid and ask are to be published by endpoint
 */
@Data
public class PriceDto {

    String instrumentName;

    BigDecimal bid;

    BigDecimal ask;

    // This is left for manual testing purposes, but it should be removed from final version once assumption
    // that timestamp in price feed is insignificant is confirmed
    String timestamp;

    public void setInstrumentName(InstrumentName instrumentName) {
        this.instrumentName = instrumentName.label;
    }

    // This will always return some value unsupported instrument names are not added to repository
    public InstrumentName getInstrumentName() {
        Optional<InstrumentName> opt = InstrumentName.valueOfLabel(this.instrumentName);
        return opt.map(instrument -> instrument)
                .orElseGet(() -> null);
    }
}
