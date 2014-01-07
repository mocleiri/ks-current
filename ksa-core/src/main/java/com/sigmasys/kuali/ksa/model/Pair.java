package com.sigmasys.kuali.ksa.model;

import java.io.Serializable;

/**
 * Pair of objects
 * <p/>
 *
 * @author Michael Ivanov
 */
public class Pair<A extends Serializable, B extends Serializable> implements Serializable {

    private A a;
    private B b;

    public Pair() {
    }

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public void setA(A a) {
        this.a = a;
    }

    public void setB(B b) {
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Pair pair = (Pair) o;

        return (!(a != null ? !a.equals(pair.a) : pair.a != null) &&  (b != null ? b.equals(pair.b) : pair.b == null));
    }

    @Override
    public int hashCode() {
        int result = a != null ? a.hashCode() : 0;
        result = 31 * result + (b != null ? b.hashCode() : 0);
        return result;
    }
}
