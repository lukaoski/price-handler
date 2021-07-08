package com.domain.pricehandler.controller;

import com.domain.pricehandler.controller.dto.PriceDto;
import com.domain.pricehandler.exceptions.PriceControllerException;
import com.domain.pricehandler.model.Price;
import com.domain.pricehandler.service.PriceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST endpoint to retrieve prices
 *
 * WebFlux was chosen to support concurrent reading and writing of prices
 */
@RestController
@RequestMapping("/prices")
public class PriceController {
    /**
     * If more time was available, more exceptions and error codes could be added, eg:
     * IncorrectCurrencyFormatException
     * UnsupportedCurrencyPairException
     * PriceFeedFailureException
     */
    private final static String UNKNOWN_ERROR_CODE = "PRICE_CONTROLLER_000";
    private final static String UNKNOWN_ERROR_MSG = "Unknown Error Occurred";

    @Autowired
    private PriceService priceService;

    @Autowired
    private ModelMapper modelMapper;

    private PriceDto convertToDto(Price price) {
        PriceDto dto = modelMapper.map(price, PriceDto.class);
        return dto;
    }

    /**
     *
     * Endpoint for retrieval of single price
     * @param instrumentName instrument for which we want to receive price, expected format is "AAA_BBB".
     * A shorer form would be AAABBB, but form with '_' is more readable
     * @return price for instrument
     */
    @GetMapping("/{instrumentName}")
    private Mono<PriceDto> getPrice(@PathVariable String instrumentName) {
        try {
            // Fail fast, just checking length of instrument name. Ideally, this should be regex checking
            // characters and '_' in the middle
            if(instrumentName.length() != 7 )
                throw new Exception("Instrument name not provided in correct format");
            String instrumentNameWithSlash = instrumentName.replace("_", "/");
            Mono<Price> price = priceService.getPrice(instrumentNameWithSlash);
            Mono<PriceDto> mono = price.flatMap(p -> Mono.just(convertToDto(p)));
            return mono;
        } catch (Exception e) {
            throw PriceControllerException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .code(UNKNOWN_ERROR_CODE)
                    .message(UNKNOWN_ERROR_MSG)
                    .message(e.getMessage())
                    .cause(e)
                    .build();
        }
    }

    /**
     *
     * /prices endpoint enables retrieval of all prices
     * @return all available prices
     */
    @GetMapping
    private Flux<PriceDto> getPrices() {
        try {
            Flux<Price> prices = priceService.getPrices();
            Flux<PriceDto> flux = prices.map(p -> convertToDto(p));
            return flux;
        } catch (Exception e) {
            throw PriceControllerException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .code(UNKNOWN_ERROR_CODE)
                    .message(UNKNOWN_ERROR_MSG)
                    .message(e.getMessage())
                    .cause(e)
                    .build();
        }
    }
}