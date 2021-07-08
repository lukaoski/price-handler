package com.domain.pricehandler.service;

import com.domain.pricehandler.model.InstrumentName;
import com.domain.pricehandler.model.Price;
import com.domain.pricehandler.service.repository.PriceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * In interest of time, decided to use integration tests only for price service
 * in scope of updates to repository.
 * Ideally, could add some price service and price repository unit tests.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PriceServiceIntegrationTests {

    @Autowired
    private PriceService priceService;

    @Autowired
    private PriceRepository priceRepository;

    // EUR/USD
    Price priceEUR_USD_initial;
    Price priceEUR_USD_new_id_higher;
    Price priceEUR_USD_new_id_lower;

    // EUR/JPY
    Price priceEUR_JPY_initial;
    Price priceEUR_JPY_new_id_higher;
    Price priceEUR_JPY_new_id_lower;

    // GBP/USD
    Price priceGBP_USD_new;
    Price priceGBP_USD_new_id_higher;

    @BeforeEach
    public void setUp() {
        priceEUR_USD_initial = new Price();
        priceEUR_USD_initial.setId(10);
        priceEUR_USD_initial.setInstrumentName(InstrumentName.EUR_USD);
        priceEUR_USD_initial.setAsk(new BigDecimal(106.0));
        priceEUR_USD_initial.setBid(new BigDecimal(105.0));

        priceEUR_JPY_initial = new Price();
        priceEUR_JPY_initial.setId(20);
        priceEUR_JPY_initial.setInstrumentName(InstrumentName.EUR_JPY);
        priceEUR_JPY_initial.setAsk(new BigDecimal(1060.0));
        priceEUR_JPY_initial.setBid(new BigDecimal(1050.0));

        List<Price> prices = new ArrayList<>();
        prices.add(priceEUR_USD_initial);
        prices.add(priceEUR_JPY_initial);

        priceService.updatePrices(prices);

        priceEUR_USD_new_id_higher = new Price();
        priceEUR_USD_new_id_higher.setId(30);
        priceEUR_USD_new_id_higher.setInstrumentName(InstrumentName.EUR_USD);
        priceEUR_USD_new_id_higher.setAsk(new BigDecimal(116.0));
        priceEUR_USD_new_id_higher.setBid(new BigDecimal(115.0));

        priceEUR_USD_new_id_lower = new Price();
        priceEUR_USD_new_id_lower.setId(8);
        priceEUR_USD_new_id_lower.setInstrumentName(InstrumentName.EUR_USD);
        priceEUR_USD_new_id_lower.setAsk(new BigDecimal(126.0));
        priceEUR_USD_new_id_lower.setBid(new BigDecimal(125.0));

        priceEUR_JPY_new_id_higher = new Price();
        priceEUR_JPY_new_id_higher.setId(21);
        priceEUR_JPY_new_id_higher.setInstrumentName(InstrumentName.EUR_JPY);
        priceEUR_JPY_new_id_higher.setAsk(new BigDecimal(1061.0));
        priceEUR_JPY_new_id_higher.setBid(new BigDecimal(1051.0));

        priceEUR_JPY_new_id_lower = new Price();
        priceEUR_JPY_new_id_lower.setId(19);
        priceEUR_JPY_new_id_lower.setInstrumentName(InstrumentName.EUR_JPY);
        priceEUR_JPY_new_id_lower.setAsk(new BigDecimal(1091.0));
        priceEUR_JPY_new_id_lower.setBid(new BigDecimal(1071.0));

        priceGBP_USD_new = new Price();
        priceGBP_USD_new.setId(30);
        priceGBP_USD_new.setInstrumentName(InstrumentName.GBP_USD);
        priceGBP_USD_new.setAsk(new BigDecimal(10061.0));
        priceGBP_USD_new.setBid(new BigDecimal(10051.0));

        priceGBP_USD_new_id_higher = new Price();
        priceGBP_USD_new_id_higher.setId(31);
        priceGBP_USD_new_id_higher.setInstrumentName(InstrumentName.GBP_USD);
        priceGBP_USD_new_id_higher.setAsk(new BigDecimal(10191.0));
        priceGBP_USD_new_id_higher.setBid(new BigDecimal(10181.0));
    }

    @AfterEach
    public void teardown() {
        priceService.clearPrices();
    }

    @Test
    void givenPrices_whenGetPriceForUnreadInstrumentName_thenReturnEmpty() {
        Mono<Price> price = priceService.getPrice(InstrumentName.GBP_USD.label);

        StepVerifier
                .create(price)
                .expectComplete()
                .verify();
    }

    @Test
    void givenPrices_whenUpdatePriceWithNewer_thenPriceUpdated() {

        List<Price> prices = new ArrayList<>();
        prices.add(priceEUR_USD_new_id_higher);

        priceService.updatePrices(prices);

        Mono<Price> priceEUR_USD_read = priceService.getPrice(InstrumentName.EUR_USD.label);

        StepVerifier
                .create(priceEUR_USD_read)
                .expectNext(priceEUR_USD_new_id_higher)
                .expectComplete()
                .verify();
    }

    @Test
    void givenPrices_whenUpdatePriceWithOlder_thenPriceNotUpdated() {

        List<Price> prices = new ArrayList<>();
        prices.add(priceEUR_USD_new_id_lower);

        priceService.updatePrices(prices);

        Mono<Price> priceEUR_USD_read = priceService.getPrice(InstrumentName.EUR_USD.label);

        StepVerifier
                .create(priceEUR_USD_read)
                .expectNext(priceEUR_USD_initial)
                .expectComplete()
                .verify();
    }

    @Test
    void givenPrices_whenUpdatePriceWithNewerOrOlderOnePerInstrumentName_thenCorrectPricesUpdated() {

        List<Price> prices = new ArrayList<>();
        prices.add(priceEUR_USD_new_id_higher);
        prices.add(priceEUR_JPY_new_id_lower);
        prices.add(priceGBP_USD_new);

        priceService.updatePrices(prices);

        Flux<Price> pricesRead = priceService.getPrices();

        Map<InstrumentName, Price> pricesReadAsMap = pricesRead
                .collectMap(
                        p -> p.getInstrumentName(),
                        p -> p)
                .block();

        assertThat(pricesReadAsMap.get(InstrumentName.EUR_USD).getId().equals(priceEUR_USD_new_id_higher.getId()));
        assertThat(pricesReadAsMap.get(InstrumentName.EUR_JPY).getId().equals(priceEUR_JPY_initial.getId()));
        assertThat(pricesReadAsMap.get(InstrumentName.GBP_USD).getId().equals(priceGBP_USD_new.getId()));
    }

    @Test
    void givenPrices_whenUpdatePriceWithNewerOrOlderOnesPerInstrumentName_thenCorrectPricesUpdated() {

        List<Price> prices = new ArrayList<>();
        prices.add(priceEUR_USD_new_id_higher);
        prices.add(priceEUR_JPY_new_id_lower);
        prices.add(priceGBP_USD_new);
        prices.add(priceEUR_USD_new_id_lower);
        prices.add(priceGBP_USD_new_id_higher);
        prices.add(priceEUR_JPY_new_id_higher);

        priceService.updatePrices(prices);

        Flux<Price> pricesRead = priceService.getPrices();

        Map<InstrumentName, Price> pricesReadAsMap = pricesRead
                .collectMap(
                        p -> p.getInstrumentName(),
                        p -> p)
                .block();

        assertThat(pricesReadAsMap.get(InstrumentName.EUR_USD).getId().equals(priceEUR_USD_new_id_higher.getId()));
        assertThat(pricesReadAsMap.get(InstrumentName.EUR_JPY).getId().equals(priceEUR_JPY_new_id_higher.getId()));
        assertThat(pricesReadAsMap.get(InstrumentName.GBP_USD).getId().equals(priceGBP_USD_new_id_higher.getId()));
    }
}