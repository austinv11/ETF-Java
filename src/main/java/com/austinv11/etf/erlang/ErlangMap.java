package com.austinv11.etf.erlang;

import com.austinv11.etf.common.TermTypes;
import com.austinv11.etf.util.BertCompatible;

import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

/**
 * This represents an immutable ETF list. 
 */
@BertCompatible
public class ErlangMap extends AbstractMap<Object, Object> implements ErlangObject {

    private final Map<Object, Object> data;

    public ErlangMap(Map<Object, Object> data) {
        this.data = data;
    }

    @Override
    public byte type() {
        return TermTypes.MAP_EXT;
    }

    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return data.entrySet();
    }

    @Override
    public Object get(Object key) {
        return data.get(key);
    }

    /**
     * Gets the specified object as an integer.
     *
     * @param key The object's key.
     * @return The object.
     */
    public int getInt(Object key) {
        return (int) get(key);
    }

    /**
     * Gets the specified object as a short.
     *
     * @param key The object's key.
     * @return The object.
     */
    public short getShort(Object key) {
        return (short) get(key);
    }

    /**
     * Gets the specified object as a character.
     *
     * @param key The object's key.
     * @return The object.
     */
    public char getChar(Object key) {
        return (char) get(key);
    }

    /**
     * Gets the specified object as a byte.
     *
     * @param key The object's key.
     * @return The object.
     */
    public byte getByte(Object key) {
        return (byte) get(key);
    }

    /**
     * Gets the specified object as a float.
     *
     * @param key The object's key.
     * @return The object.
     */
    public float getFloat(Object key) {
        return (float) get(key);
    }

    /**
     * Gets the specified object as a long.
     *
     * @param key The object's key.
     * @return The object.
     */
    public long getLong(Object key) {
        return (long) get(key);
    }

    /**
     * Gets the specified object as a boolean.
     *
     * @param key The object's key.
     * @return The object.
     */
    public boolean getBoolean(Object key) {
        return (boolean) get(key);
    }

    /**
     * Gets the specified object as a nil.
     *
     * @param key The object's key.
     * @return The object.
     */
    public void getNil(Object key) {}

    /**
     * Gets the specified object as a string.
     *
     * @param key The object's key.
     * @return The object.
     */
    public String getString(Object key) {
        return (String) get(key);
    }

    /**
     * Gets the specified object as a BigInteger.
     *
     * @param key The object's key.
     * @return The object.
     */
    public BigInteger getBigInteger(Object key) {
        return (BigInteger) get(key);
    }

    /**
     * Gets the specified object as an ErlangList.
     *
     * @param key The object's key.
     * @return The object.
     */
    public ErlangList getErlangList(Object key) {
        return (ErlangList) get(key);
    }

    /**
     * Gets the specified object as an ErlangMap.
     *
     * @param key The object's key.
     * @return The object.
     */
    public ErlangMap getErlangMap(Object key) {
        return (ErlangMap) get(key);
    }

    /**
     * Gets the specified object as a Fun.
     *
     * @param key The object's key.
     * @return The object.
     */
    public Fun getFun(Object key) {
        return (Fun) get(key);
    }

    /**
     * Gets the specified object as a PID.
     *
     * @param key The object's key.
     * @return The object.
     */
    public PID getPID(Object key) {
        return (PID) get(key);
    }

    /**
     * Gets the specified object as a Port.
     *
     * @param key The object's key.
     * @return The object.
     */
    public Port getPort(Object key) {
        return (Port) get(key);
    }

    /**
     * Gets the specified object as a Reference.
     *
     * @param key The object's key.
     * @return The object.
     */
    public Reference getReference(Object key) {
        return (Reference) get(key);
    }

    /**
     * Gets the specified object as a Tuple.
     *
     * @param key The object's key.
     * @return The object.
     */
    public Tuple getTuple(Object key) {
        return (Tuple) get(key);
    }

    /**
     * Gets the specified object as binary.
     *
     * @param key The object's key.
     * @return The object.
     */
    public byte[] getBinary(Object key) {
        return (byte[]) get(key);
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
