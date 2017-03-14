package com.austinv11.etf.parsing;

import com.austinv11.etf.ETFConfig;
import com.austinv11.etf.erlang.*;
import com.austinv11.etf.util.BertCompatible;
import com.austinv11.etf.util.ETFException;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.zip.Inflater;

import static com.austinv11.etf.common.TermTypes.*;

/**
 * This represents a parser for parsing data from an etf object.
 */
public class ETFParser {

    private final byte[] data;
    private int offset = 0;
    private final int expectedVersion;
    private final boolean bert;
    private final boolean loqui;

    public ETFParser(byte[] data, ETFConfig config) {
        this(data, config, false);
    }

    public ETFParser(byte[] data, ETFConfig config, boolean partial) {
        this.expectedVersion = config.getVersion();
        this.bert = config.isBert();
        this.loqui = config.isLoqui();

        int initialOffset = 0;
        if (Byte.toUnsignedInt(data[initialOffset]) == expectedVersion) //Skip the version number
            initialOffset++;

        if (!partial && config.isIncludingHeader()) {
            if (data[initialOffset] != HEADER)
                throw new ETFException("Missing header! Is this data malformed?");
            initialOffset++;

            long uncompressedSize = wrap(data, initialOffset, 4).getInt();
            initialOffset += 4;
            
            Inflater inflater = new Inflater();
            inflater.setInput(Arrays.copyOfRange(data, initialOffset, data.length));
            
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
                byte[] buffer = new byte[1024];
                while (inflater.getBytesWritten() != uncompressedSize) {
                    int count = inflater.inflate(buffer);
                    outputStream.write(buffer, 0, count);
                }
                inflater.end();
                outputStream.close();
                this.data = outputStream.toByteArray();
            } catch (Exception e) {
                throw new ETFException(e);
            }
        } else {
            this.data = data;
        }
    }

    private static ByteBuffer wrap(byte[] array) {
        return ByteBuffer.wrap(array).order(ByteOrder.BIG_ENDIAN);
    }

    private static ByteBuffer wrap(byte[] array, int offset, int length) {
        return ByteBuffer.wrap(array, offset, length).order(ByteOrder.BIG_ENDIAN);
    }

    private void skipVersion() {
        if (Byte.toUnsignedInt(data[offset]) == expectedVersion)
            offset++;
    }

    /**
     * This gets the number of bytes in the uncompressed term data.
     *
     * @return The number of bytes.
     */
    public int getSize() {
        return data.length;
    }

    /**
     * Gets the version of etf this is using.
     *
     * @return The version.
     */
    public int getVersion() {
        return expectedVersion;
    }

    /**
     * Checks if this parser is BERT compatible.
     *
     * @return True when BERT compatible, false if otherwise.
     */
    public boolean isBert() {
        return bert;
    }

    /**
     * This gets the raw term data (excluding the initial distribution header.
     *
     * @return The raw data.
     */
    public byte[] getRawData() {
        return data;
    }

    /**
     * This gets the current position the parser is at in the raw data array.
     *
     * @return The current array offset.
     */
    public int getPosition() {
        return offset;
    }

    /**
     * This checks if there is no more data to read.
     *
     * @return True when there is no more data to read, false when otherwise.
     */
    public boolean isFinished() {
        return offset >= data.length;
    }

    private void checkPreconditions() throws ETFException {
        checkPreconditions(null);
    }

    private void checkPreconditions(byte type) throws ETFException {
        checkPreconditions(type, null);
    }

    private void checkPreconditions(Boolean bertStatus) throws ETFException {
        checkPreconditions(-1, bertStatus);
    }

    private void checkPreconditions(int type, Boolean bertStatus) throws ETFException {
        if (bertStatus != null) { //bert status is relevant
            if (bertStatus != isBert())
                throw new ETFException("BERT vs ETF spec mismatch");
        }

        if (isFinished()) {
            throw new ETFException("No more data to read!");
        }

        skipVersion();

        if (type != -1) {
            if (type != peek()) {
                throw new ETFException("ETF Term type mismatch!");
            } else {
                offset++;
            }
        }
    }

    /**
     * This peeks at the type of the next term.
     *
     * @return The type of the next term.
     *
     * @see com.austinv11.etf.common.TermTypes
     */
    public byte peek() {
        checkPreconditions();

        return data[offset];
    }

    /**
     * This gets the next distribution header.
     *
     * @return The next header.
     */
    public DistributionHeader nextDistributionHeader() { //TODO?
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * This gets an index referring to an atom cache reference in the distribution header.
     *
     * @return The index.
     *
     * @see #nextDistributionHeader()
     */
    public short nextAtomCacheIndex() {
        checkPreconditions(ATOM_CACHE_REF, false);

        return (short) Byte.toUnsignedInt(data[offset++]);
    }

    /**
     * This gets the next small integer (unsigned 8 bit int).
     *
     * @return The int.
     */
    @BertCompatible
    public int nextSmallInt() {
        checkPreconditions(SMALL_INTEGER_EXT);

        return Byte.toUnsignedInt(data[offset++]);
    }

    /**
     * This gets the next large integer (signed 32 bit).
     *
     * @return The int.
     */
    @BertCompatible
    public int nextLargeInt() {
        checkPreconditions(INTEGER_EXT);

        int integer = wrap(data, offset, 4).getInt();

        offset += 4;

        return integer;
    }

    /**
     * This gets the next large or small integer.
     *
     * @return The int.
     */
    @BertCompatible
    public int nextInt() {
        byte type = peek();

        if (type == SMALL_INTEGER_EXT) {
            return nextSmallInt();
        } else {
            return nextLargeInt();
        }
    }

    /**
     * This gets the next old formatted float.
     *
     * @return The float.
     */
    @BertCompatible
    public double nextOldFloat() {
        checkPreconditions(FLOAT_EXT);

        return Float.parseFloat(new String(Arrays.copyOfRange(data, offset, (offset += 31))));
    }

    /**
     * This gets the next new formatted float.
     *
     * @return The float.
     */
    public double nextNewFloat() {
        checkPreconditions(NEW_FLOAT_EXT, false);

        double num = wrap(data, offset, 8).getDouble();

        offset += 8;

        return num;
    }

    /**
     * This gets the next new or old float.
     *
     * @return The float.
     */
    public double nextFloat() {
        byte version = peek();

        if (version == FLOAT_EXT) {
            return nextOldFloat();
        } else {
            return nextNewFloat();
        }
    }

    /**
     * Gets the next large Latin-1 encoded atom.
     *
     * @return The atom name.
     */
    @BertCompatible
    public String nextLargeAtom() {
        checkPreconditions(ATOM_EXT);

        char len = wrap(data, offset, 2).getChar(); //Because we don't have unsigned shorts
        offset += 2;

        try {
            return new String(Arrays.copyOfRange(data, offset, (offset += len)), "ISO-8859-1" /*Latin-1 charset*/);
        } catch (UnsupportedEncodingException e) {
            throw new ETFException(e);
        }
    }

    /**
     * Gets the next small Latin-1 encoded atom.
     *
     * @return The atom name.
     */
    public String nextSmallAtom() {
        checkPreconditions(SMALL_ATOM_EXT, false);

        int len = Byte.toUnsignedInt(data[offset++]);

        try {
            return new String(Arrays.copyOfRange(data, offset, (offset += len)), "ISO-8859-1" /*Latin-1 charset*/);
        } catch (UnsupportedEncodingException e) {
            throw new ETFException(e);
        }
    }

    /**
     * Gets the next large UTF-8 encoded atom.
     *
     * @return The atom name.
     */
    public String nextLargeUTF8Atom() {
        checkPreconditions(ATOM_UTF8_EXT, false);

        char len = wrap(data, offset, 2).getChar(); //Because we don't have unsigned shorts
        offset += 2;

        try {
            return new String(Arrays.copyOfRange(data, offset, (offset += len)), "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new ETFException(e);
        }
    }

    /**
     * Gets the next small UTF-8 encoded atom.
     *
     * @return The atom name.
     */
    public String nextSmallUTF8Atom() {
        checkPreconditions(SMALL_ATOM_UTF8_EXT, false);

        int len = Byte.toUnsignedInt(data[offset++]);

        try {
            return new String(Arrays.copyOfRange(data, offset, (offset += len)), "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new ETFException(e);
        }
    }

    /**
     * Gets the next atom (small or large and latin-1 or utf-8).
     *
     * @return The atom name.
     */
    public String nextAtom() {
        byte type = peek();

        if (type == SMALL_ATOM_EXT) {
            return nextSmallAtom();
        } else if (type == ATOM_EXT) {
            return nextLargeAtom();
        } else if (type == ATOM_UTF8_EXT) {
            return nextLargeUTF8Atom();
        } else {
            return nextSmallUTF8Atom();
        }
    }

    /**
     * Gets the next boolean (expects the boolean to be encoded as an atom!),
     *
     * @return The next boolean encoded as an atom.
     */
    public boolean nextBoolean() {
        if (!loqui)
            throw new ETFException("Loqui booleans not supported!");

        String atom;

        if (peek() == SMALL_ATOM_EXT)
            atom = nextSmallAtom();
        else
            atom = nextSmallUTF8Atom();

        return Boolean.parseBoolean(atom);
    }

    /**
     * This gets the next binary representation of a list or term.
     *
     * @return The binary data.
     */
    @BertCompatible
    public byte[] nextBinary() {
        checkPreconditions(BINARY_EXT);

        long len = Integer.toUnsignedLong(wrap(data, offset, 4).getInt());
        offset += 4;

        return Arrays.copyOfRange(data, offset, (offset += len));
    }

    /**
     * This gets the next bitstring.
     *
     * @return The binary data.
     */
    public String nextBitBinary() {
        checkPreconditions(BIT_BINARY_EXT);

        long len = Integer.toUnsignedLong(wrap(data, offset, 4).getInt());
        offset += 4;

        byte bits = data[offset++];

        byte[] bytes = new byte[(int) len];
        for (int i = 0; i < len; i+=4) {
            byte val = data[offset++];

            if (i == len-1) //Tail
                val >>>= 8-bits; //bits = # of significant bits from 1-8, so we remove the insignificant ones

            bytes[i] = val;
        }

        return new String(Arrays.copyOfRange(bytes, 0, (int) len));
    }

    /**
     * Gets the next "string". NOTE: Erlang doesn't natively support strings, strings are actually just unsigned byte
     * lists (or char list in java). So the string might be nonsensical.
     *
     * @return The string.
     *
     * @see String#toCharArray()
     */
    @BertCompatible
    public String nextErlangString() {
        checkPreconditions(STRING_EXT);

        char len = wrap(data, offset, 2).getChar(); //Because we don't have unsigned shorts
        offset += 2;

        return new String(Arrays.copyOfRange(data, offset, (offset += len)));
    }

    /**
     * This gets the next atom or string.
     *
     * @return The atom or string.
     */
    public String nextString() {
        byte type = peek();

        if (type == STRING_EXT) {
            return nextErlangString();
        } else if (type == BIT_BINARY_EXT) {
            return nextBitBinary();
        } else if (type == BINARY_EXT) {
            return new String(nextBinary());
        } else {
            return nextAtom();
        }
    }

    private Node nextNode() {
        int type = peek();

        String atom = null;
        int index = -1;

        if (type == ATOM_EXT) { //Only supports standard atoms + atom index
            atom = nextLargeAtom();
        } else if (type == SMALL_ATOM_EXT) {
            atom = nextSmallAtom();
        } else {
            index = nextAtomCacheIndex();
        }

        if (index != -1) {
            return new Node(index);
        } else {
            return new Node(atom);
        }
    }

    /**
     * Gets the next port object.
     *
     * @return The port object.
     */
    public Port nextPort() { //Pretty much identical to #nextReference
        checkPreconditions(PORT_EXT, false);

        Node node = nextNode();

        int id = wrap(data, offset, 4).getInt();
        offset += 4;

        byte creation = data[offset++];

        if (node.isRef()) {
            return new Port(node.ref, id, creation);
        } else {
            return new Port(node.atom, id, creation);
        }
    }

    /**
     * Gets the next pid object.
     *
     * @return The pid object.
     */
    public PID nextPID() {
        checkPreconditions(PID_EXT, false);

        Node node = nextNode();

        int id = wrap(data, offset, 4).getInt();
        offset += 4;

        int serial = wrap(data, offset, 4).getInt();
        offset += 4;

        byte creation = data[offset++];

        if (node.isRef()) {
            return new PID(node.ref, id, serial, creation);
        } else {
            return new PID(node.atom, id, serial, creation);
        }
    }

    private Tuple findTuple(long arity) {
        Object[] data = new Object[(int)arity];
        for (int i = 0; i < arity; i++) {
            data[i] = next();
        }

        return new Tuple(data);
    }

    /**
     * Gets the next small tuple.
     *
     * @return The tuple.
     */
    public Tuple nextSmallTuple() {
        checkPreconditions(SMALL_TUPLE_EXT);

        return findTuple(Byte.toUnsignedInt(data[offset++]));
    }

    /**
     * Gets the next large tuple.
     *
     * @return The tuple.
     */
    @BertCompatible
    public Tuple nextLargeTuple() {
        checkPreconditions(LARGE_TUPLE_EXT);

        long arity = Integer.toUnsignedLong(wrap(data, offset, 4).getInt());
        offset += 4;
        
        Tuple tuple = findTuple(arity);

        return tuple;
    }

    /**
     * Gets the next small or large tuple.
     *
     * @return The tuple.
     */
    @BertCompatible
    public Tuple nextTuple() {
        byte type = peek();

        if (type == SMALL_TUPLE_EXT) {
            return nextSmallTuple();
        } else {
            return nextLargeTuple();
        }
    }

    /**
     * Gets the next map.
     *
     * @return The map.
     */
    @BertCompatible
    public ErlangMap nextMap() {
        checkPreconditions(MAP_EXT);

        long arity = Integer.toUnsignedLong(wrap(data, offset, 4).getInt());
        offset += 4;

        Map<Object, Object> map = new HashMap<>();
        for (long i = 0; i < arity; i++) {
            map.put(next(), next());
        }

        return new ErlangMap(map);
    }

    /**
     * Checks if the next term is nil.
     *
     * @return True if the next term is nil, false if otherwise.
     */
    @BertCompatible
    public boolean isNil() {
        if (peek() == NIL_EXT || !loqui)
           return peek() == NIL_EXT;
        else if (peek() == SMALL_ATOM_EXT || peek() == SMALL_ATOM_UTF8_EXT) {//Because Discord's api is annoying
            int initialOffset = offset;
            String atom = nextAtom();
            offset = initialOffset; //Reset offset because it wasn't nil!
            return atom.equals("nil");
        } else {
            return false;
        }
    }

    /**
     * Gets the next nil.
     */
    @BertCompatible
    public void nextNil() {
        if (peek() == NIL_EXT || !loqui)
            checkPreconditions(NIL_EXT); //Offset should be incremented here
        else if (peek() == SMALL_ATOM_EXT || peek() == SMALL_ATOM_UTF8_EXT) {//Because Discord's api is annoying
            int initialOffset = offset;
            if (!nextAtom().equals("nil"))
                offset = initialOffset; //Reset offset because it wasn't nil!
        }
    }

    /**
     * Gets the next proper or improper list.
     *
     * @return The list.
     */
    @BertCompatible
    public ErlangList nextList() {
        checkPreconditions(LIST_EXT);

        long len = Integer.toUnsignedLong(wrap(data, offset, 4).getInt());
        offset += 4;

        Object[] list = new Object[(int) len];
        for (int i = 0; i < len; i++) {
            list[i] = next();
        }

        Object tail;
        if (isNil()) { //Proper list
            nextNil();
            tail = null;
        } else {
            tail = next();
        }

        return new ErlangList(list, tail);
    }

    private long nextBig(long len) {
        int sign = Byte.toUnsignedInt(data[offset++]);
        
        long total = 0L;
        //Sorry for this algorithm but its what the docs say to do
        for (long i = 0; i < len; i++) {
            total += Byte.toUnsignedInt(data[offset++]) * (long)Math.pow(256, i);
        }

        if (sign == 0) { //Positive
            if (total < 0)
                total *= -1;
        } else if (sign == 1) { //Negative
            if (total > 0)
                total *= -1;
        }

        return total;
    }

    /**
     * Gets the next small big number.
     *
     * @return The small big number.
     */
    @BertCompatible
    public long nextSmallBig() {
        checkPreconditions(SMALL_BIG_EXT);

        return nextBig(Byte.toUnsignedInt(data[offset++]));
    }

    /**
     * Gets the next large big number.
     *
     * @return The large big number.
     */
    @BertCompatible
    public long nextLargeBig() {
        checkPreconditions(LARGE_BIG_EXT);

        long len = Integer.toUnsignedLong(wrap(data, offset, 4).getInt());
        offset += 4;

        return nextBig(len);
    }

    /**
     * Gets the next big number.
     *
     * @return The big number.
     */
    @BertCompatible
    public long nextBigNumber() {
        if (peek() == SMALL_BIG_EXT) {
            return nextSmallBig();
        } else {
            return nextLargeBig();
        }
    }

    /**
     * Gets the next old reference object.
     *
     * @return The old reference object.
     */
    public Reference nextOldReference() {
        checkPreconditions(REFERENCE_EXT, false);

        Node node = nextNode();

        long id = Integer.toUnsignedLong(wrap(data, offset, 4).getInt());
        offset += 4;

        byte creation = data[offset++];

        if (node.isRef()) {
            return new Reference(node.ref, id, creation);
        } else {
            return new Reference(node.atom, id, creation);
        }
    }

    /**
     * Gets the next new reference object.
     *
     * @return The new reference object.
     */
    public Reference nextNewReference() {
        checkPreconditions(NEW_REFERENCE_EXT, false);

        char len = wrap(data, offset, 2).getChar(); //Because we don't have unsigned shorts
        offset += 2;

        Node node = nextNode();

        byte creation = data[offset++];

        long[] id = new long[len];
        for (char i = 0; i < len; i++) {
            id[i] = Integer.toUnsignedLong(wrap(data, offset, 4).getInt());
            offset += 4;
        }

        if (node.isRef()) {
            return new Reference(node.ref, creation, id);
        } else {
            return new Reference(node.atom, creation, id);
        }
    }

    /**
     * Gets the next reference object.
     *
     * @return The reference object.
     */
    public Reference nextReference() {
        if (peek() == REFERENCE_EXT) {
            return nextOldReference();
        } else {
            return nextNewReference();
        }
    }

    /**
     * Gets the next old function reference.
     *
     * @return The function object.
     */
    public Fun nextOldFun() {
        throw new UnsupportedOperationException("Not implemented"); //TODO?
    }

    /**
     * Gets the next new function reference.
     *
     * @return The function object.
     */
    public Fun nextNewFun() {
        throw new UnsupportedOperationException("Not implemented"); //TODO?
    }

    /**
     * Gets the next export function reference.
     *
     * @return The function object.
     */
    public Fun nextExport() {
        throw new UnsupportedOperationException("Not implemented"); //TODO?
    }

    /**
     * Gets the next old/new/export function reference.
     *
     * @return The function object.
     */
    public Fun nextFun() {
        byte type = peek();

        if (type == FUN_EXT) {
            return nextOldFun();
        } else if (type == NEW_FUN_EXT) {
            return nextNewFun();
        } else {
            return nextExport();
        }
    }

    //TODO: Implement advanced BERT objs

    private Object handleWeirdAtoms(String atom) { //Because Discord's api is annoying
        if (!loqui)
            return atom;

        switch (atom) {
            case "true":
            case "false":
                return Boolean.parseBoolean(atom);
            case "nil":
                return null;
            default:
                return atom;
        }
    }

    /**
     * This gets the next generic term.
     *
     * @return The next term.
     */
    @BertCompatible
    public Object next() {
        switch (peek()) {
            case HEADER:
                throw new ETFException("Nested header found! Is the data malformed?");
            case DISTRIBUTION_HEADER:
                return nextDistributionHeader();
            case ATOM_CACHE_REF:
                return nextAtomCacheIndex();
            case SMALL_INTEGER_EXT:
                return nextSmallInt();
            case INTEGER_EXT:
                return nextLargeInt();
            case FLOAT_EXT:
                return nextOldFloat();
            case ATOM_EXT:
                return handleWeirdAtoms(nextLargeAtom());
            case REFERENCE_EXT:
                return nextOldReference();
            case PORT_EXT:
                return nextPort();
            case PID_EXT:
                return nextPID();
            case SMALL_TUPLE_EXT:
                return nextSmallTuple();
            case LARGE_TUPLE_EXT:
                return nextLargeTuple();
            case MAP_EXT:
                return nextMap();
            case NIL_EXT:
                nextNil();
                return null;
            case STRING_EXT:
                return nextErlangString();
            case LIST_EXT:
                return nextList();
            case BINARY_EXT:
                return nextBinary();
            case SMALL_BIG_EXT:
                return nextSmallBig();
            case LARGE_BIG_EXT:
                return nextLargeBig();
            case NEW_REFERENCE_EXT:
                return nextNewReference();
            case SMALL_ATOM_EXT:
                return handleWeirdAtoms(nextSmallAtom());
            case FUN_EXT:
                return nextOldFun();
            case NEW_FUN_EXT:
                return nextNewFun();
            case EXPORT_EXT:
                return nextExport();
            case BIT_BINARY_EXT:
                return nextBitBinary();
            case NEW_FLOAT_EXT:
                return nextNewFloat();
            case ATOM_UTF8_EXT:
                return handleWeirdAtoms(nextLargeUTF8Atom());
            case SMALL_ATOM_UTF8_EXT:
                return handleWeirdAtoms(nextSmallUTF8Atom());
            default:
                throw new ETFException("Unidentified type " + peek() + " is the data malformed?");
        }
    }

    /**
     * This reads all of the terms in the provided etf data from the current offset.
     *
     * @return The list of all remaining terms.
     */
    public List<Object> readFully() {
        List<Object> terms = new ArrayList<>();
        while (!isFinished()) {
            terms.add(next());
        }

        return terms;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        for (int i = 0; i < data.length; i++) {
            builder.append(data[i]);
            if (i+1 != data.length)
                builder.append(", ");
        }
        builder.append(">");

        return builder.toString();
    }

    //Internal use only, we don't actually provide a Node object
    private class Node {
        final String atom;
        final int ref;

        public Node(String atom) {
            this.atom = atom;
            this.ref = -1;
        }

        public Node(int ref) {
            this.ref = ref;
            this.atom = null;
        }

        boolean isRef() {
            return ref != -1;
        }
    }
}
