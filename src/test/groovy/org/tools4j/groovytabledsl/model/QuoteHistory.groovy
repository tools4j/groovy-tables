package org.tools4j.groovytabledsl.model

import groovy.transform.EqualsAndHashCode
import org.tools4j.groovytabledsl.model.Side

/**
 * User: ben
 * Date: 23/03/2016
 * Time: 5:20 PM
 */


@EqualsAndHashCode
class QuoteHistory {
    final String symbol
    final Side side
    final double[] prices

    QuoteHistory(final String symbol, final Side side, final double[] prices) {
        this.symbol = symbol
        this.side = side
        this.prices = prices
    }

    @Override
    public String toString() {
        return "QuoteHistory{" +
                "symbol='" + symbol + '\'' +
                ", side=" + side +
                ", prices=" + Arrays.toString(prices) +
                '}';
    }
}
