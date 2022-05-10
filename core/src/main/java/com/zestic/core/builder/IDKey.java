package com.zestic.core.builder;

import java.io.Serializable;

final class IDKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Object value;
    private final int id;

    public IDKey(final Object obj) {
        id = System.identityHashCode(obj);
        // There have been some cases (LANG-459) that return the
        // same identity hash code for different objects. So
        // the value is also added to disambiguate these cases.
        value = obj;
    }

    /*
     * returns hashcode - i.e. the system identity hashcode.
     *
     * @return the hashcode
     */
    @Override public int hashCode() {
        return id;
    }

    /*
     * checks if instances are equal
     *
     * @param other The other object to compare to
     * @return if the instances are for the same object
     */
    @Override public boolean equals(final Object other) {
        if (!(other instanceof IDKey)) {
            return false;
        }
        final IDKey idKey = (IDKey) other;
        if (id != idKey.id) {
            return false;
        }
        // Note that identity equals is used.
        return value == idKey.value;
    }
}
