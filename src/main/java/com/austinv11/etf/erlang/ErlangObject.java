package com.austinv11.etf.erlang;

/**
 * This represents a generic erlang object.
 */
public interface ErlangObject {

    /**
     * This gets the type number for this object.
     *
     * @return The type.
     */
    byte type();

    /**
     * This converts an object to etf compatible bytes.
     *
     * @return The ETF binary bytes.
     */
    byte[] toBytes();
}
