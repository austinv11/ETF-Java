package com.austinv11.etf.erlang;

import com.austinv11.etf.common.TermTypes;
import com.austinv11.etf.util.BertCompatible;

import java.math.BigInteger;
import java.util.*;

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
        Object obj = data.get(key);
        if (obj instanceof byte[])
            return new String((byte[]) obj);
        return obj;
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
        Object obj = get(key);
        if (obj instanceof String)
            return (String) obj;
        else
            return new String((byte[]) obj);
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
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        data.forEach((k, v) -> builder.append(objToString(k)).append("=").append(objToString(v)).append(", "));
        if (builder.length() > 2)
            builder.replace(builder.length() - 2, builder.length(), "");
        builder.append("}");
        return builder.toString();
    }
    
    private String objToString(Object val) {
        if (val == null)
            return "null";
        else if (val instanceof byte[])
            return new String((byte[]) val);
        else if (val.getClass().isArray()) {
            if (val instanceof char[]) {
                return Arrays.toString((char[]) val);
            } else if (val instanceof int[]) {
                return Arrays.toString((int[]) val);
            } else if (val instanceof long[]) {
                return Arrays.toString((long[]) val);
            } else if (val instanceof float[]) {
                return Arrays.toString((float[]) val);
            } else if (val instanceof double[]) {
                return Arrays.toString((double[]) val);
            } else if (val instanceof boolean[]) {
                return Arrays.toString((boolean[]) val);
            } else {
                return Arrays.toString((Object[]) val);
            }
        } else
            return val.toString();
    }
}
