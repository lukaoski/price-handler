package com.domain.pricehandler.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Enum with Supported instrument names
 */
public enum InstrumentName {
    EUR_USD("EUR/USD"),
    EUR_JPY("EUR/JPY"),
    GBP_USD("GBP/USD");

    // enumSet added to support checking if requested instrument name is in enum
    private static EnumSet<InstrumentName> enumSet;

    public final String label;

    private InstrumentName(String label) {
        this.label = label;
    }

    private static final Map<String, InstrumentName> BY_LABEL = new HashMap<>();

    static {
        for (InstrumentName i: values()) {
            BY_LABEL.put(i.label, i);
        }

        enumSet = EnumSet.allOf(InstrumentName.class);
    }

    /**
     * Returns instrument name enum if supported
     * @param label
     * @return
     */
    public static Optional<InstrumentName> valueOfLabel(String label) {
        return Optional.ofNullable(BY_LABEL.get(label));
    }

    /**
     * Checks if instrument name is supported
     * @param instrumentName
     * @return
     */
    public static boolean contains(InstrumentName instrumentName) {
        return enumSet.contains(instrumentName);
    }
}
