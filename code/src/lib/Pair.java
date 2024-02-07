package lib;


/**
 *  Utitlity class to represent a pair
 */
public class Pair<K,V> {
    private K first;
    private V second;

    /** creates a pair <code>(first, second)</code>
     *
     * @param first the first component of the pair
     * @param second the second component of the pair
     */
    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }
    
    public void setFirst(K _first) {
    	this.first = _first;
    }
    
    public void setSecond(V _second) {
    	this.second = _second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
        return second != null ? second.equals(pair.second) : pair.second == null;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }
}
