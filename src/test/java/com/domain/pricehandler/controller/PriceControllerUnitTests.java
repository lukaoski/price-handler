package com.domain.pricehandler.controller;

import com.domain.pricehandler.controller.dto.PriceDto;
import com.domain.pricehandler.model.InstrumentName;
import com.domain.pricehandler.model.Price;
import com.domain.pricehandler.service.PriceServiceImpl;
import com.domain.pricehandler.service.repository.PriceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = PriceController.class)
@Import(PriceServiceImpl.class)
public class PriceControllerUnitTests {

    @MockBean
    PriceRepository repository;

    @Autowired
    private WebTestClient webClient;

    List<Price> prices;

    /**
     * With more time given, input to test cases could be moved to another script
     */
    @BeforeEach
    public void init() {
        Price priceEUR_USD = new Price();
        priceEUR_USD.setId(1);
        priceEUR_USD.setInstrumentName(InstrumentName.EUR_USD);
        priceEUR_USD.setBid(new BigDecimal(101.0));
        priceEUR_USD.setAsk(new BigDecimal(102.0));

        Price priceEUR_JPY = new Price();
        priceEUR_JPY.setId(2);
        priceEUR_JPY.setInstrumentName(InstrumentName.EUR_JPY);
        priceEUR_JPY.setBid(new BigDecimal(103.0));
        priceEUR_JPY.setAsk(new BigDecimal(104.0));

        Price priceGBP_USD = new Price();
        priceGBP_USD.setId(3);
        priceGBP_USD.setInstrumentName(InstrumentName.GBP_USD);
        priceGBP_USD.setBid(new BigDecimal(105.0));
        priceGBP_USD.setAsk(new BigDecimal(106.0));

        prices = new ArrayList<Price>();
        prices.add(priceEUR_USD);
        prices.add(priceEUR_JPY);
        prices.add(priceGBP_USD);
    }

    @AfterEach
    public void teardown() {
        prices.clear();
    }

    @Test
    void givenPrices_whenGetPrices_thenStatus200() {

        Mockito
                .when(repository.get())
                .thenReturn(Optional.ofNullable(prices));

        webClient.get()
                .uri("/prices/")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PriceDto.class);

        Mockito.verify(repository, times(1)).get();
    }

    @Test
    void givenPrices_whenGetPrice_thenStatus200() {
        InstrumentName instrumentName = InstrumentName.EUR_USD;

        List<Price> filteredPrices = prices
                .stream()
                .filter(p -> p.getInstrumentName() == InstrumentName.EUR_USD)
                .collect(Collectors.toList());

        Optional<Price> opt = Optional.ofNullable(filteredPrices.get(0));

        Mockito
                .when(repository.getByInstrumentName(instrumentName))
                .thenReturn(opt);

        webClient.get()
                .uri("/prices/{instrumentName}", instrumentName.label)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.instrumentName").isNotEmpty()
                .jsonPath("$.ask").isEqualTo(102.0)
                .jsonPath("$.bid").isEqualTo(101.0);

        Mockito.verify(repository, times(1)).getByInstrumentName(instrumentName);
    }
}