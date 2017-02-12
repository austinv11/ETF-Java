package com.austinv11.etf.erlang;

import com.austinv11.etf.common.TermTypes;
import com.austinv11.etf.util.BertCompatible;

import java.math.BigInteger;
import java.util.AbstractList;

/**
 * This represents an immutable ETF tuple.
 * NOTE: This can also be a BERT "advanced" type if the first object is "bert".
 */
@BertCompatible
public class Tuple extends AbstractList<Object> implements ErlangObject {

    private final Object[] data;

    public Tuple(Object[] data) {
        this.data = data;
    }

    /**
     * Returns the array containing all the tuple's objects.
     *
     * @return The objects.
     */
    public Object[] getAllObjects() {
        return data;
    }

    @Override
    public Object get(int index) {
        return data[index];
    }

    @Override
    public int size() {
        return data.length;
    }

    /**
     * Gets the specified object as an integer.
     *
     * @param index The object's index.
     * @return The object.
     */
    public int getInt(int index) {
        return (int) data[index];
    }

    /**
     * Gets the specified object as a short.
     *
     * @param index The object's index.
     * @return The object.
     */
    public short getShort(int index) {
        return (short) data[index];
    }

    /**
     * Gets the specified object as a character.
     *
     * @param index The object's index.
     * @return The object.
     */
    public char getChar(int index) {
        return (char) data[index];
    }

    /**
     * Gets the specified object as a byte.
     *
     * @param index The object's index.
     * @return The object.
     */
    public byte getByte(int index) {
        return (byte) data[index];
    }

    /**
     * Gets the specified object as a float.
     *
     * @param index The object's index.
     * @return The object.
     */
    public float getFloat(int index) {
        return (float) data[index];
    }

    /**
     * Gets the specified object as a long.
     *
     * @param index The object's index.
     * @return The object.
     */
    public long getLong(int index) {
        return (long) data[index];
    }

    /**
     * Gets the specified object as a boolean.
     *
     * @param index The object's index.
     * @return The object.
     */
    public boolean getBoolean(int index) {
        return (boolean) data[index];
    }

    /**
     * Gets the specified object as a nil.
     *
     * @param index The object's index.
     * @return The object.
     */
    public void getNil(int index) {}

    /**
     * Gets the specified object as a string.
     *
     * @param index The object's index.
     * @return The object.
     */
    public String getString(int index) {
        return (String) data[index];
    }

    /**
     * Gets the specified object as a BigInteger.
     *
     * @param index The object's index.
     * @return The object.
     */
    public BigInteger getBigInteger(int index) {
        return (BigInteger) data[index];
    }

    /**
     * Gets the specified object as an ErlangList.
     *
     * @param index The object's index.
     * @return The object.
     */
    public ErlangList getErlangList(int index) {
        return (ErlangList) data[index];
    }

    /**
     * Gets the specified object as an ErlangMap.
     *
     * @param index The object's index.
     * @return The object.
     */
    public ErlangMap getErlangMap(int index) {
        return (ErlangMap) data[index];
    }

    /**
     * Gets the specified object as a Fun.
     *
     * @param index The object's index.
     * @return The object.
     */
    public Fun getFun(int index) {
        return (Fun) data[index];
    }

    /**
     * Gets the specified object as a PID.
     *
     * @param index The object's index.
     * @return The object.
     */
    public PID getPID(int index) {
        return (PID) data[index];
    }

    /**
     * Gets the specified object as a Port.
     *
     * @param index The object's index.
     * @return The object.
     */
    public Port getPort(int index) {
        return (Port) data[index];
    }

    /**
     * Gets the specified object as a Reference.
     *
     * @param index The object's index.
     * @return The object.
     */
    public Reference getReference(int index) {
        return (Reference) data[index];
    }

    /**
     * Gets the specified object as a Tuple.
     *
     * @param index The object's index.
     * @return The object.
     */
    public Tuple getTuple(int index) {
        return (Tuple) data[index];
    }

    /**
     * Gets the specified object as binary.
     *
     * @param index The object's index.
     * @return The object.
     */
    public byte[] getBinary(int index) {
        return (byte[]) data[index];
    }

    /**
     * This returns true if this is considered a "small" tuple by erlang.
     *
     * @return True when small, false when large.
     */
    public boolean isSmall() {
        return data.length <= 255;
    }

    /**
     * Checks if this is a bert object.
     *
     * @return True if a bert object, false if otherwise.
     */
    public boolean isBertObject() {
        return data.length > 0 && data[0] instanceof String &&  data[0].equals("bert");
    }

    @Override
    public byte type() {
        return isSmall() ? TermTypes.SMALL_TUPLE_EXT : TermTypes.LARGE_TUPLE_EXT;
    }
}
