package com.austinv11.etf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.austinv11.etf.Terms.*;

/**
 * A class which manages a buffer of ETF data.
 */
public class EtfBuffer implements Closeable {

    private final ByteBuf buffer;

    private EtfBuffer(ByteBuf buffer) {
        buffer.retain();
        this.buffer = buffer;
    }

    /**
     * Builds a heap-bound ETF buffer.
     * @return The buffer.
     */
    public static EtfBuffer onHeapBuffer() {
        return new EtfBuffer(Unpooled.buffer());
    }

    /**
     * Builds a heap-less ETF buffer.
     * @return The buffer.
     */
    public static EtfBuffer directBuffer() {
        return new EtfBuffer(Unpooled.directBuffer());
    }

    /**
     * Wraps a byte buffer for reading only.
     * @param bb The byte buffer to wrap.
     * @return The buffer.
     */
    public static EtfBuffer wrapForRead(ByteBuf bb) {
        return new EtfBuffer(bb);
    }

    /**
     * Returns a read-only view over the internal buffer.
     * @return The buffer.
     */
    public ByteBuf write() {
        return buffer.asReadOnly();
    }

    /**
     * Starts writing to the buffer. This isn't explicitly required, but ETF parsers may not understand the output
     * unless you explicitly start writing (only once!).
     * @return The buffer for chaining.
     */
    public EtfBuffer startWrite() {
        buffer.writeByte(EtfConstants.VERSION);
        return this;
    }

    /**
     * Starts reading from the buffer.
     * @return The buffer for reading.
     */
    public EtfBuffer startRead() {
        assert buffer.readByte() == EtfConstants.VERSION;
        return this;
    }

    /**
     * Release the resources being used.
     *
     * @return The buffer for chaining.
     */
    public EtfBuffer endWrite() {
        close();
        return this;
    }

    /**
     * Release the resources being used.
     *
     * @return The buffer for chaining.
     */
    public EtfBuffer endRead() {
        close();
        return this;
    }

    @Override
    public void close() {
        buffer.release();
    }

    private EtfBuffer write(byte... bytes) {
        for (byte b : bytes) {
            buffer.writeByte(b);
        }
        return this;
    }

    private EtfBuffer write(int... bytes) {
        for (int b : bytes) {
            buffer.writeByte(b);
        }
        return this;
    }

    private void requireType(byte type) {
        if (peek() != type)
            throw new EtfException("Unexpected type " + type);
        buffer.readByte();
    }

    private byte requireEitherType(byte... types) {
        byte peeked = peek();
        for (byte t : types) {
            if (t == peeked) {
                return buffer.readByte();
            }
        }
        throw new EtfException("Unexpected type " + peeked);
    }

    /**
     * Peeks at the next byte.
     * @return The next byte.
     */
    public byte peek() {
        int i = buffer.readerIndex();
        return buffer.getByte(i);
    }

    /**
     * Writes a small integer (unsigned byte).
     * @return The buffer for chaining.
     */
    public EtfBuffer writeSmallInt(byte b) {
        return write(SMALL_INTEGER_EXT, b);
    }

    /**
     * Reads a small integer (unsigned byte).
     * @return The integer.
     */
    public byte readSmallInt() {
        requireType(SMALL_INTEGER_EXT);
        return buffer.readByte();
    }

    /**
     * Writes a signed integer.
     * @param i The integer.
     * @return The buffer for chaining.
     */
    public EtfBuffer writeInt(int i) {
        return write(INTEGER_EXT, ((i >>> 24) & 0xff),
                ((i >>> 16) & 0xff),
                ((i >>> 8) & 0xff),
                (i & 0xff));
    }

    /**
     * Reads a signed integer.
     * @return The integer.
     */
    public int readInt() {
        requireType(INTEGER_EXT);
        return (buffer.readByte() << 24)
                | (buffer.readByte() << 16)
                | (buffer.readByte() << 8)
                | buffer.readByte();
    }

    /**
     * Indicates that a tuple will be written.
     * @param count The number of tuple entries (as an unsigned integer). This indicates how many write operations
     *  will be counted as part of the tuple.
     * @return The buffer for chaining.
     */
    public EtfBuffer startWriteTuple(int count) {
        return startWriteTuple(count, count < 256);
    }

    /**
     * Starts reading a tuple.
     * @return The number of elements in the tuple.
     */
    public int startReadTuple() {
        byte type = requireEitherType(SMALL_TUPLE_EXT, LARGE_TUPLE_EXT);
        if (type == SMALL_TUPLE_EXT) {
            return buffer.readUnsignedByte();
        } else {
            return (buffer.readByte() << 24)
                    | (buffer.readByte() << 16)
                    | (buffer.readByte() << 8)
                    | buffer.readByte();
        }
    }

    /**
     * Indicates that a tuple will be written.
     * @param count The number of tuple entries (as an unsigned integer). This indicates how many write operations
     *  will be counted as part of the tuple.
     * @param small Whether this should be explicitly set as a small tuple or large tuple.
     * @return The buffer for chaining.
     */
    public EtfBuffer startWriteTuple(int count, boolean small) {
        if (small) {
            return write(SMALL_TUPLE_EXT, count);
        } else {
            return write(LARGE_TUPLE_EXT, ((count >>> 24) & 0xff),
                    ((count >>> 16) & 0xff),
                    ((count >>> 8) & 0xff),
                    (count & 0xff));
        }
    }

    /**
     * Indicates that a map will be written.
     * @param count The number of K-V pairs (as an unsigned integer). This indicates the number of write opertions times
     *  2 that will be counted as part of the map. The following elements are written as K1, V1, ..., Kn, Vn where n is
     *  the count.
     * @return The buffer for chaining.
     */
    public EtfBuffer startWriteMap(int count) {
        return write(MAP_EXT, ((count >>> 24) & 0xff),
                ((count >>> 16) & 0xff),
                ((count >>> 8) & 0xff),
                (count & 0xff));
    }

    /**
     * Starts reading a map.
     * @return The number of K-V pairs in the map.
     */
    public int startReadMap() {
        requireType(MAP_EXT);
        return (buffer.readByte() << 24)
                | (buffer.readByte() << 16)
                | (buffer.readByte() << 8)
                | buffer.readByte();
    }

    /**
     * Writes nil.
     *
     * @return The buffer for chaining.
     */
    public EtfBuffer writeNil() {
        return write(NIL_EXT);
    }

    /**
     * Reads nil.
     */
    public void readNil() {
        requireType(NIL_EXT);
    }

    /**
     * Writes a string of bytes.
     * @param bytes The bytes.
     * @return The buffer for chaining.
     */
    public EtfBuffer writeString(byte[] bytes) {
        write(STRING_EXT,
                ((bytes.length >>> 8) & 0xff),
                (bytes.length & 0xff));
        return write(bytes);
    }

    /**
     * Reads a string of bytes.
     * @return The bytes.
     */
    public byte[] readString() {
        requireType(STRING_EXT);
        int count = (buffer.readByte() << 8)
                | buffer.readByte();
        byte[] bytes = new byte[count];
        buffer.readBytes(bytes);
        return bytes;
    }

    /**
     * Starts writing a list. NOTE: A list in ETF is terminated with a tail value (so the list contains count + 1
     * elements in total), this tail is usually nil (proper list).
     * @param count The number of elements of the list (unsigned).
     * @return The buffer for chaining.
     *
     * @see #writeNil()
     */
    public EtfBuffer startWriteList(int count) {
        return write(LIST_EXT, ((count >>> 24) & 0xff),
                ((count >>> 16) & 0xff),
                ((count >>> 8) & 0xff),
                (count & 0xff));
    }

    /**
     * Starts reading a list. NOTE: A list in ETF is terminated with a tail value (so the list contains count + 1
     * elements in total), this tail is usually nil (proper list).
     * @return The number of elements (excluding the tail).
     */
    public int startReadList() {
        requireType(LIST_EXT);
        return (buffer.readByte() << 24)
                | (buffer.readByte() << 16)
                | (buffer.readByte() << 8)
                | buffer.readByte();
    }

    /**
     * Writes a floating point number.
     *
     * @param f The number.
     * @return The buffer for chaining.
     */
    public EtfBuffer writeFloat(double f) {
        long longBits = Double.doubleToLongBits(f);
        return write(NEW_FLOAT_EXT,
                (byte) ((longBits >>> 56) & 0xff), (byte) ((longBits >>> 48) & 0xff),
                (byte) ((longBits >>> 40) & 0xff), (byte) ((longBits >>> 32) & 0xff),
                (byte) ((longBits >>> 24) & 0xff), (byte) ((longBits >>> 16) & 0xff),
                (byte) ((longBits >>> 8) & 0xff), (byte) (longBits & 0xff));
    }

    /**
     * Reads a floating point number.
     * @return The number.
     */
    public double readFloat() {
        requireType(NEW_FLOAT_EXT);
        long bits = ((long) buffer.readByte() << 56)
                | ((long) buffer.readByte() << 48)
                | ((long) buffer.readByte() << 40)
                | ((long) buffer.readByte() << 32)
                | ((long) buffer.readByte() << 24)
                | ((long) buffer.readByte() << 16)
                | ((long) buffer.readByte() << 8)
                | buffer.readByte();
        return Double.longBitsToDouble(bits);
    }

    /**
     * Write a utf-8 atom.
     * @param s The string to write.
     * @return The buffer for chaining.
     */
    public EtfBuffer writeAtom(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        return writeAtom(bytes, bytes.length < 256);
    }

    /**
     * Read a utf-8 atom. NOTE: In loqui (Discord), an atom of "nil" is a nil value and "true"/"false" atoms are
     * booleans.
     * @return The atom.
     */
    public String readAtom() {
        byte type = requireEitherType(SMALL_ATOM_UTF8_EXT, ATOM_UTF8_EXT);

        int length;
        if (type == SMALL_TUPLE_EXT) {
            length = buffer.readUnsignedByte();
        } else {
            length = (buffer.readByte() << 8)
                    | buffer.readByte();
        }

        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }


    /**
     * Writes a utf-8 atom with explicit typing.
     * @param s The string to write.
     * @param small True if a small atom, false for a normal atom.
     * @return The buffer for chaining.
     */
    public EtfBuffer writeAtom(String s, boolean small) {
        return writeAtom(s.getBytes(StandardCharsets.UTF_8), small);
    }

    private EtfBuffer writeAtom(byte[] s, boolean small) {
        if (small) {
            write(SMALL_ATOM_UTF8_EXT, (s.length & 0xff));
        } else {
            write(ATOM_UTF8_EXT, ((s.length >>> 8) & 0xff), (s.length & 0xff));
        }

        for (byte b : s) {
            write(b);
        }
        return this;
    }

    // Loqui (discord) specific differences

    /**
     * Writes a boolean as a small atom. (Loqui specific).
     * @param bool The boolean.
     * @return The buffer for chaining.
     */
    public EtfBuffer writeBoolean(boolean bool) {
        return writeAtom(bool ? "true" : "false");
    }

    /**
     * Reads a boolean from an atom. (Loqui specific).
     * @return The boolean.
     */
    public boolean readBoolean() {
        String atom = readAtom();
        if ("true".equals(atom)) {
            return true;
        } else if ("false".equals(atom)) {
            return false;
        } else {
            throw new EtfException("Atom is not a boolean!");
        }
    }

    /**
     * Writes "nil" as an atom. (Loqui specific encoding for nil).
     * @return The buffer for chaining.
     */
    public EtfBuffer writeLoquiNil() {
        return writeAtom("nil");
    }

    /**
     * Reads "nil" as an atom. (Loqui specific).
     */
    public void readLoquiNil() {
        if (!"nil".equals(readAtom())) {
            throw new EtfException("Atom is not nil!");
        }
    }
}
