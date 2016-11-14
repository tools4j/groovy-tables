package org.tools4j.groovytables.model

/**
 * User: ben
 * Date: 11/11/2016
 * Time: 5:38 PM
 */
class QuoteBook {
    final List<Quote> bids = new ArrayList<>()
    final List<Quote> asks = new ArrayList<>()

    public List<Quote> getSide(Side side){
        if( side == Side.BUY ){
            return bids
        } else {
            return asks
        }
    }

    @Override
    public String toString() {
        return "QuoteBook{" +
                "bids=" + bids +
                ", asks=" + asks +
                '}';
    }
}
