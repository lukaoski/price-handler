package com.domain.pricehandler.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Model of price
 *
 * Assumption was made that price feed message unique id is enough to identify the newest price,
 * hence timestamp is not being used for this purpose, nor is it published in endpoint
 *
 * Also, ideally Price Feed class could be separated from Price class
 */
@Data
public class Price {

    Integer id;

    // Ideally, instrument name could be moved out of Price class to avoid redundancy in repository
    InstrumentName instrumentName;

    // BigDecimal used as this is currency
    BigDecimal bid;

    // BigDecimal used as this is currency
    BigDecimal ask;

    /**
     *  Assumption was made that this field is not needed to identify the newest price, as we can identify
     *  the newest price per instrument name based on id.
     *  Hence, this field is read from price feed, but never utilized - any transformations from
     *  and to date from/to string would be less efficient than checks done with id.
     *
     *  If I had more time, I would consider using this field to:
     *  1) Validate that price published in REST is not stale - we could decide to not show prices if time elapsed
     *  from last price feed generation (so, not necessarily, when we received the price feed) is greater
     *  than some threshold value
     */
    String timestamp;
}
