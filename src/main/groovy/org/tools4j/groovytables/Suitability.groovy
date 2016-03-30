package org.tools4j.groovytables

/**
 * User: ben
 * Date: 12/02/2016
 * Time: 6:27 AM
 */
public enum Suitability {
    NOT_SUITABLE(0),
    SUITABLE_WITH_COERCION(1),
    SUITABLE_WITH_EXPECTED_COERCION(2),
    SUITABLE_BY_UP_CASTING(3),
    SUITABLE(4);

    private final int ranking;

    private Suitability(final int ranking) {
        this.ranking = ranking
    }

    public static Suitability leastSuitableOf(final Suitability arg1, final Suitability arg2){
        if(arg1.ranking <= arg2.ranking){
            return arg1;
        } else {
            return arg2;
        }
    }

    public Suitability worseOf(final Suitability other){
        return leastSuitableOf(this, other);
    }

    public boolean isMoreSuitableThan(final Suitability other) {
        return this.ranking > other.ranking
    }

    public boolean isLessSuitableThan(final Suitability other) {
        return this.ranking < other.ranking
    }
}
