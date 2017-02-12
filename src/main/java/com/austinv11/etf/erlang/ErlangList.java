package com.austinv11.etf.erlang;

import com.austinv11.etf.common.TermTypes;
import com.austinv11.etf.util.BertCompatible;

import java.math.BigInteger;
import java.util.AbstractList;
import java.util.List;

/**
 * This represents an immutable ETF list.
 */
@BertCompatible
public class ErlangList extends AbstractList<Object> implements ErlangObject {

    private final List<Object> data;
    private final Object tail;

    public ErlangList(List<Object> data, Object tail) {
        this.data = data;
        this.tail = tail;
    }

    @Override
    public Object get(int index) {
        if (index == data.size())
            return tail;

        return data.get(index);
    }

    @Override
    public int size() {
        return isProper() ? data.size() : data.size()+1;
    }

    /**
     * Gets the specified object as an integer.
     *
     * @param index The object's index.
     * @return The object.
     */
    public int getInt(int index) {
        return (int) get(index);
    }

    /**
     * Gets the specified object as a short.
     *
     * @param index The object's index.
     * @return The object.
     */
    public short getShort(int index) {
        return (short) get(index);
    }

    /**
     * Gets the specified object as a character.
     *
     * @param index The object's index.
     * @return The object.
     */
    public char getChar(int index) {
        return (char) get(index);
    }

    /**
     * Gets the specified object as a byte.
     *
     * @param index The object's index.
     * @return The object.
     */
    public byte getByte(int index) {
        return (byte) get(index);
    }

    /**
     * Gets the specified object as a float.
     *
     * @param index The object's index.
     * @return The object.
     */
    public float getFloat(int index) {
        return (float) get(index);
    }

    /**
     * Gets the specified object as a long.
     *
     * @param index The object's index.
     * @return The object.
     */
    public long getLong(int index) {
        return (long) get(index);
    }

    /**
     * Gets the specified object as a boolean.
     *
     * @param index The object's index.
     * @return The object.
     */
    public boolean getBoolean(int index) {
        return (boolean) get(index);
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
        return (String) get(index);
    }

    /**
     * Gets the specified object as a BigInteger.
     *
     * @param index The object's index.
     * @return The object.
     */
    public BigInteger getBigInteger(int index) {
        return (BigInteger) get(index);
    }

    /**
     * Gets the specified object as an ErlangList.
     *
     * @param index The object's index.
     * @return The object.
     */
    public ErlangList getErlangList(int index) {
        return (ErlangList) get(index);
    }

    /**
     * Gets the specified object as an ErlangMap.
     *
     * @param index The object's index.
     * @return The object.
     */
    public ErlangMap getErlangMap(int index) {
        return (ErlangMap) get(index);
    }

    /**
     * Gets the specified object as a Fun.
     *
     * @param index The object's index.
     * @return The object.
     */
    public Fun getFun(int index) {
        return (Fun) get(index);
    }

    /**
     * Gets the specified object as a PID.
     *
     * @param index The object's index.
     * @return The object.
     */
    public PID getPID(int index) {
        return (PID) get(index);
    }

    /**
     * Gets the specified object as a Port.
     *
     * @param index The object's index.
     * @return The object.
     */
    public Port getPort(int index) {
        return (Port) get(index);
    }

    /**
     * Gets the specified object as a Reference.
     *
     * @param index The object's index.
     * @return The object.
     */
    public Reference getReference(int index) {
        return (Reference) get(index);
    }

    /**
     * Gets the specified object as a Tuple.
     *
     * @param index The object's index.
     * @return The object.
     */
    public Tuple getTuple(int index) {
        return (Tuple) get(index);
    }

    /**
     * Gets the specified object as binary.
     *
     * @param index The object's index.
     * @return The object.
     */
    public byte[] getBinary(int index) {
        return (byte[]) get(index);
    }

    /**
     * Gets if this list is proper (Tail is nil/null).
     *
     * @return True when proper, false when improper.
     * @see #getTail()
     */
    public boolean isProper() {
        return tail == null;
    }

    /**
     * Gets the tail of this list.
     *
     * @return The tail (this is null when this is a proper list).
     * @see #isProper()
     */
    public Object getTail() {
        return tail;
    }

    @Override
    public byte type() {
        return TermTypes.LIST_EXT;
    }
}
