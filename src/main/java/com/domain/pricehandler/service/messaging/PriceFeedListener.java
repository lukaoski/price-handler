package com.domain.pricehandler.service.messaging;

/**
 * Responsible for receiving price feeds
 */
public interface PriceFeedListener {

    public void onMessage(String message );
}
