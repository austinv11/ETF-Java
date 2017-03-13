package com.austinv11.etf.writing;

import com.austinv11.etf.ETFConfig;
import com.austinv11.etf.erlang.*;
import com.austinv11.etf.util.ETFException;
import com.austinv11.etf.util.ReflectionUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;

import static com.austinv11.etf.common.TermTypes.*;

/**
 * This represents a writer for writing data to an etf object.
 */
public class ETFWriter {

    private byte[] data = new byte[64];
    private int offset = 0;
    private final boolean bert;
    private final byte version;
    private final boolean includeHeader;
    private final boolean includeDistributionHeader;
    private final boolean loqui;
    private final boolean compress;

    public ETFWriter(ETFConfig config) {
        this(config, false);
    }

    public ETFWriter(ETFConfig config, boolean partial) {
        if (partial) { //These should never be true when partial
            includeDistributionHeader = false;
            includeHeader = false;
            compress = false; //Can't compress without a header
        } else {
            includeDistributionHeader = config.isIncludingDistributionHeader();
            includeHeader = config.isIncludingHeader();
            compress = config.isCompressing();
        }
        bert = config.isBert();
        version = (byte) config.getVersion();
        loqui = config.isLoqui();
    }

    private void writeToBuffer(byte... data) {
        if (this.data.length - offset < data.length+1/*Ensure room for a version byte if necessary*/) { //We need to expand the buffer
            this.data = Arrays.copyOf(this.data, this.data.length * 2);
        }

        if (this.data[0] != version && !includeDistributionHeader) {
           this.data[offset++] = version;
        }

        for (byte b : data) {
            this.data[offset++] = b;
        }
    }

    public ETFWriter writeAtomCacheIndex(short index) {
        writeToBuffer(ATOM_CACHE_REF, (byte) index);
        return this;
    }

    public ETFWriter writeSmallInt(int integer) {
        writeToBuffer(SMALL_INTEGER_EXT, (byte) (integer & 0xff));
        return this;
    }

    public ETFWriter writeLargeInt(int integer) {
        writeToBuffer(INTEGER_EXT, (byte) ((integer >>> 24) & 0xff), (byte) ((integer >>> 16) & 0xff), 
                (byte) ((integer >>> 8) & 0xff), (byte) (integer & 0xff));
        return this;
    }

    public ETFWriter writeInt(int integer) {
        if (Integer.BYTES * integer == 1) {
            writeSmallInt((short) integer);
        } else {
            writeLargeInt(integer);
        }
        return this;
    }
    
    public strictfp ETFWriter writeOldFloat(double num) {
        writeToBuffer(FLOAT_EXT);
        writeToBuffer(String.format("%.20f", num).getBytes());
        return this;
    }
    
    public strictfp ETFWriter writeNewFloat(double num) {
        long longBits = Double.doubleToLongBits(num);
        writeToBuffer(NEW_FLOAT_EXT, 
                (byte) ((longBits >>> 56) & 0xff), (byte) ((longBits >>> 48) & 0xff), 
                (byte) ((longBits >>> 40) & 0xff), (byte) ((longBits >>> 32) & 0xff), 
                (byte) ((longBits >>> 24) & 0xff), (byte) ((longBits >>> 16) & 0xff),
                (byte) ((longBits >>> 8) & 0xff), (byte) (longBits & 0xff));
        return this;
    }
    
    public ETFWriter writeFloat(double num) { //TODO: Add header checks
        return writeNewFloat(num);
    }
    
    public ETFWriter writeLargeAtom(String atom) {
        try {
            byte[] bytes = atom.getBytes("ISO-8859-1" /*Latin-1 charset*/);
            writeToBuffer(ATOM_EXT);
            writeToBuffer((byte) ((bytes.length >>> 8) & 0xff), (byte) (bytes.length & 0xff)); //Length number
            writeToBuffer(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new ETFException(e);
        }
        return this;
    }
    
    public ETFWriter writeSmallAtom(String atom) {
        try {
            byte[] bytes = atom.getBytes("ISO-8859-1" /*Latin-1 charset*/);
            writeToBuffer(SMALL_ATOM_EXT);
            writeToBuffer((byte) (bytes.length & 0xff)); //Length number
            writeToBuffer(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new ETFException(e);
        }
        return this;
    }
    
    public ETFWriter writeLargeUTF8Atom(String atom) {
        try {
            byte[] bytes = atom.getBytes("UTF8");           
            writeToBuffer(ATOM_EXT);
            writeToBuffer((byte) ((bytes.length >>> 8) & 0xff), (byte) (bytes.length & 0xff)); //Length number
            writeToBuffer(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new ETFException(e);
        }
        return this;
    }
    
    public ETFWriter writeSmallUTF8Atom(String atom) {
        try {
            byte[] bytes = atom.getBytes("UTF8");
            writeToBuffer(SMALL_ATOM_EXT);
            writeToBuffer((byte) (bytes.length & 0xff)); //Length number
            writeToBuffer(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new ETFException(e);
        }
        return this;
    }
    
    public ETFWriter writeAtom(String atom) {
        //TODO Header check for UTF8
        if (atom.length() > 256)
            writeLargeAtom(atom);
        else
            writeSmallAtom(atom);
    
//        if (atom.length() > 256)
//            writeLargeUTF8Atom(atom);
//        else
//            writeSmallUTF8Atom(atom);
        return this;
    }
    
    public ETFWriter writeBoolean(boolean bool) {
        if (!loqui)
            throw new ETFException("Loqui booleans not supported!");
        
        return writeAtom(bool ? "true" : "false");
    }
    
    public ETFWriter writeBinary(String bin) {
        return writeBinary(bin.getBytes());
    }
    
    public ETFWriter writeBinary(byte[] bin) {
        writeToBuffer(BINARY_EXT);
        writeToBuffer((byte) ((bin.length >>> 24) & 0xff), (byte) ((bin.length >>> 16) & 0xff),
                (byte) ((bin.length >>> 8) & 0xFF), (byte) (bin.length & 0xff));
        writeToBuffer(bin);
        return this;
    }
    
    public ETFWriter writeBitString(String string) {
        byte[] bytes = string.getBytes();
        writeToBuffer(BINARY_EXT);
        writeToBuffer((byte) ((bytes.length >>> 24) & 0xff), (byte) ((bytes.length >>> 16) & 0xff),
                (byte) ((bytes.length >>> 8) & 0xFF), (byte) (bytes.length & 0xff));
        int unsigned = Byte.toUnsignedInt(bytes[bytes.length-1]);
        int i = 1;
        while (i < unsigned)
            i <<= 1;
        writeToBuffer((byte) (i-1));
        writeToBuffer(bytes);
        return this;
    }
    
    public ETFWriter writeErlangString(String string) {
        char[] chars = string.toCharArray();
        writeToBuffer(STRING_EXT);
        writeToBuffer((byte) ((chars.length >>> 8) & 0xff), (byte) (chars.length & 0xff));
        for (char character : chars)
            writeToBuffer((byte) character);
        return this;
    }
    
    public ETFWriter writePort(Port port) {
        //TODO
        return this;
    }
    
    public ETFWriter writePID(PID pid) {
        //TODO
        return this;
    }
    
    public <T> ETFWriter writeSmallTuple(Collection<T> tuple) {
        int arity = (tuple.size() & 0xFF);
        writeToBuffer(SMALL_TUPLE_EXT, (byte) arity);
        Iterator<T> iterator = tuple.iterator();
        for (int i = 0; i < arity; i++)
            write(iterator.next());
        return this;
    }
    
    public <T> ETFWriter writeSmallTuple(T[] tuple) {
        int arity = (tuple.length & 0xFF);
        writeToBuffer(SMALL_TUPLE_EXT, (byte) arity);
        for (int i = 0; i < arity; i++)
            write(tuple[i]);
        return this;
    }
    
    //TODO primitive tuples
    
    public <T> ETFWriter writeLargeTuple(Collection<T> tuple) {
        writeToBuffer(LARGE_TUPLE_EXT, (byte) ((tuple.size() >>> 24) & 0xFF),
                (byte) ((tuple.size() >>> 16) & 0xFF), (byte) ((tuple.size() >>> 8) & 0xFF),
                (byte) (tuple.size() & 0xFF));
        Iterator<T> iterator = tuple.iterator();
        for (int i = 0; i < tuple.size(); i++)
            write(iterator.next());
        return this;
    }
    
    public <T> ETFWriter writeLargeTuple(T[] tuple) {
        writeToBuffer(LARGE_TUPLE_EXT, (byte) ((tuple.length >>> 24) & 0xFF),
                (byte) ((tuple.length >>> 16) & 0xFF), (byte) ((tuple.length >>> 8) & 0xFF),
                (byte) (tuple.length & 0xFF));
        for (int i = 0; i < tuple.length; i++)
            write(tuple[i]);
        return this;
    }
    
    //TODO primitive tuples
    
    public <T> ETFWriter writeTuple(Collection<T> tuple) {
        if (tuple.size() > 256)
            writeSmallTuple(tuple);
        else
            writeLargeTuple(tuple);
        return this;
    }
    
    public <T> ETFWriter writeTuple(T[] tuple) {
        if (tuple.length > 256)
            writeSmallTuple(tuple);
        else
            writeLargeTuple(tuple);
        return this;
    }
    
    //TODO primitive tuples
    
    public <K, V> ETFWriter writeMap(Map<K, V> map) {
        writeToBuffer(MAP_EXT, (byte) ((map.size() >>> 24) & 0xFF),
                (byte) ((map.size() >>> 16) & 0xFF), (byte) ((map.size() >>> 8) & 0xFF),
                (byte) (map.size() & 0xFF));
        for (K key : map.keySet()) {
            write(key);
            write(map.get(key));
        }
        return this;
    }
    
    public ETFWriter writeMap(Object o) {
        if (o instanceof Map) {
            return writeMap((Map) o);
        } else {
            Map<String, Object> properties = new HashMap<>();
            for (ReflectionUtils.PropertyManager property : ReflectionUtils.findProperties(o, o.getClass()))
                properties.put(property.getName(), property.getValue());
            return writeMap(properties);
        }
    }
    
    public ETFWriter writeNil() {
        if (loqui)
            writeAtom("nil");
        else
            writeToBuffer(NIL_EXT);
        return this;
    }
    
    public <T> ETFWriter writeList(Collection<T> list) {
        writeToBuffer(LIST_EXT, (byte) ((list.size() >>> 24) & 0xFF),
                (byte) ((list.size() >>> 16) & 0xFF), (byte) ((list.size() >>> 8) & 0xFF),
                (byte) (list.size() & 0xFF));
        for (T obj : list)
            write(obj);
        writeNil(); //The tail is nil so that this can be a proper list
        return this;
    } 
    
    public <T> ETFWriter writeList(T[] list) {
        writeToBuffer(LIST_EXT, (byte) ((list.length >>> 24) & 0xFF),
                (byte) ((list.length >>> 16) & 0xFF), (byte) ((list.length >>> 8) & 0xFF),
                (byte) (list.length & 0xFF));
        for (T obj : list)
            write(obj);
        writeNil(); //The tail is nil so that this can be a proper list
        return this;
    }
    
    //TODO primitive lists
    
    public ETFWriter writeSmallBig(BigInteger num) {
        if (num.equals(BigInteger.ZERO)) {
            writeToBuffer(SMALL_BIG_EXT, (byte) 0, (byte) 0);
        } else {
            byte signum = num.signum() == -1 ? (byte) 1 : (byte) 0;
            num = num.abs();
            int n = (int) Math.ceil(num.bitLength()/8)+1; //Equivalent to Math.ceil(log256(num)) + 1
            byte[] bytes = new byte[n];
            writeToBuffer(SMALL_BIG_EXT, (byte) (n & 0xFF), signum);
            n -= 1;
            while (n >= 0) {
                BigInteger[] res = num.divideAndRemainder(BigInteger.valueOf(256).pow(n));
                bytes[n] = res[0].byteValue(); //Quotient
                num = res[1]; //Remainder
                n--;
            }
            writeToBuffer(bytes);
        }
        return this;
    }
    
    public ETFWriter writeSmallBig(long num) {
        if (num == 0) {
            writeToBuffer(SMALL_BIG_EXT, (byte) 0, (byte) 0);
        } else {
            byte signum = num < 0 ? (byte) 1 : (byte) 0;
            num = Math.abs(num);
            int n = (int) Math.ceil(Math.log(num)/Math.log(256))+1; //Equivalent to Math.ceil(log256(num)) + 1
            byte[] bytes = new byte[n];
            writeToBuffer(SMALL_BIG_EXT, (byte) (n & 0xFF), signum);
            n -= 1;
            while (n >= 0) {
                long rem = num%(long) Math.pow(256, n);
                long quo = num/(long) Math.pow(256, n);
                bytes[n] = (byte) quo;
                num = rem;
                n--;
            }
            writeToBuffer(bytes);
        }
        return this;
    }
    
    public ETFWriter writeLargeBig(BigInteger num) {
        if (num.equals(BigInteger.ZERO)) {
            writeToBuffer(LARGE_BIG_EXT, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
        } else {
            byte signum = num.signum() == -1 ? (byte) 1 : (byte) 0;
            num = num.abs();
            int n = (int) Math.ceil(num.bitLength()/8)+1; //Equivalent to Math.ceil(log256(num)) + 1
            byte[] bytes = new byte[n];
            writeToBuffer(LARGE_BIG_EXT, (byte) ((n >>> 24) & 0xFF), (byte) ((n >>> 16) & 0xFF),
                    (byte) ((n >>> 8) & 0xFF), (byte) (n & 0xFF), signum);
            n -= 1;
            while (n >= 0) {
                BigInteger[] res = num.divideAndRemainder(BigInteger.valueOf(256).pow(n));
                bytes[n] = res[0].byteValue(); //Quotient
                num = res[1]; //Remainder
                n--;
            }
            writeToBuffer(bytes);
        }
        return this;
    }
    
    public ETFWriter writeLargeBig(long num) {
        if (num == 0) {
            writeToBuffer(LARGE_BIG_EXT, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
        } else {
            byte signum = num < 0 ? (byte) 1 : (byte) 0;
            num = Math.abs(num);
            int n = (int) Math.ceil(Math.log(num)/Math.log(256))+1; //Equivalent to Math.ceil(log256(num)) + 1
            byte[] bytes = new byte[n];
            writeToBuffer(LARGE_BIG_EXT, (byte) ((n >>> 24) & 0xFF), (byte) ((n >>> 16) & 0xFF),
                    (byte) ((n >>> 8) & 0xFF), (byte) (n & 0xFF), signum);
            n -= 1;
            while (n >= 0) {
                long rem = num%(long) Math.pow(256, n);
                long quo = num/(long) Math.pow(256, n);
                bytes[n] = (byte) quo;
                num = rem;
                n--;
            }
            writeToBuffer(bytes);
        }
        return this;
    }
    
    public ETFWriter writeBigNumber(BigInteger num) {
        if ((int) Math.ceil(num.bitLength() / 8) + 1 > 256) //Equivalent to Math.ceil(log256(num)) + 1)
			writeLargeBig(num);
        else
            writeSmallBig(num);
        return this;
    }
    
    public ETFWriter writeBigNumber(Long num) {
        if ((int) Math.ceil(Math.log(num) / Math.log(256)) + 1 > 256) //Equivalent to Math.ceil(log256(num)) + 1)
			writeLargeBig(num);
		else
			writeSmallBig(num);
        return this;
    }
    
    public ETFWriter writeOldReference(Reference reference) {
        //TODO
        return this;
    }
    
    public ETFWriter writeNewReference(Reference reference) {
        //TODO
        return this;
    }
    
    public ETFWriter writeReference(Reference reference) {
        //TODO
        return this;
    }
    
    public ETFWriter writeOldFun(Fun fun) {
        //TODO
        return this;
    }
    
    public ETFWriter writeNewFun(Fun fun) {
        //TODO
        return this;
    }
    
    public ETFWriter writeExport(Fun fun) {
        //TODO
        return this;
    }
    
    public ETFWriter writeFun(Fun fun) {
        //TODO
        return this;
    }

    /**
     * This writes a supported object to ETF.
     *
     * @param o The object to write.
     *
     * @throws com.austinv11.etf.util.ETFException When the object isn't supported.
     */
    public void write(Object o) {
        if (o == null) {
            writeNil();
            return;
        } else if (o instanceof Number) {
            if (o instanceof BigInteger) {
                writeBigNumber((BigInteger) o);
                return;
            } else if (o instanceof Short || o instanceof Byte || o instanceof Integer) {
                writeInt(((Number) o).intValue());
                return;
            } else if (o instanceof Long) {
                writeBigNumber((long) o);
                return;
            } else if (o instanceof Float || o instanceof Double) {
                writeFloat(((Number) o).doubleValue());
                return;
            }
        } else if (o instanceof Boolean) {
            writeBoolean((Boolean) o);
            return;
        } else if (o instanceof Character) {
            writeAtom(o.toString());
            return;
        } else if (o instanceof ErlangObject) {
            if (o instanceof DistributionHeader) {
                //TODO
                return;
            } else if (o instanceof ErlangList) {
                writeList((ErlangList) o);
                return;
            } else if (o instanceof ErlangMap) {
                writeMap((ErlangMap) o);
                return;
            } else if (o instanceof Fun) {
                //TODO
                return;
            } else if (o instanceof PID) {
                //TODO
                return;
            } else if (o instanceof Port) {
                //TODO
                return;
            } else if (o instanceof Reference) {
                //TODO
                return;
            } else if (o instanceof Tuple) {
                writeTuple((Tuple) o);
                return;
            }
        } else if (o instanceof Map) {
            writeMap((Map) o);
            return;
        } else if (o instanceof Collection) {
            if (o instanceof List)
                writeList((List) o);
            else
                writeTuple((Collection) o);
            return;
        } else if (o.getClass().isArray()) {
            if (o instanceof byte[] || o instanceof Byte[]) {
                if (o instanceof Byte[]) {
                    byte[] newArray = new byte[((Byte[]) o).length];
                    for (int i = 0; i < newArray.length; i++)
                        newArray[i] = ((Byte[]) o)[i];
                    o = newArray;
                }
                writeBinary((byte[]) o);
                return;
            } else if (o instanceof char[] || o instanceof Character[]) {
                if (o instanceof Character[]) {
                    char[] newArray = new char[((Character[]) o).length];
                    for (int i = 0; i < newArray.length; i++)
                        newArray[i] = ((Character[]) o)[i];
                    o = newArray;
                }
                writeAtom(new String((char[]) o)); //TODO should we optimize for other types?
                return;
            } else {
                if (o instanceof boolean[]) {
                    Boolean[] newArray = new Boolean[((boolean[]) o).length];
                    for (int i = 0; i < newArray.length; i++)
                        newArray[i] = ((boolean[]) o)[i];
                    o = newArray;
                } else if (o instanceof short[]) {
                    Short[] newArray = new Short[((short[]) o).length];
                    for (int i = 0; i < newArray.length; i++)
                        newArray[i] = ((short[]) o)[i];
                    o = newArray;
                } else if (o instanceof int[]) {
                    Integer[] newArray = new Integer[((int[]) o).length];
                    for (int i = 0; i < newArray.length; i++)
                        newArray[i] = ((int[]) o)[i];
                    o = newArray;
                } else if (o instanceof long[]) {
                    Long[] newArray = new Long[((long[]) o).length];
                    for (int i = 0; i < newArray.length; i++)
                        newArray[i] = ((long[]) o)[i];
                    o = newArray;
                } else if (o instanceof float[]) {
                    Float[] newArray = new Float[((float[]) o).length];
                    for (int i = 0; i < newArray.length; i++)
                        newArray[i] = ((float[]) o)[i];
                    o = newArray;
                } else if (o instanceof double[]) {
                    Double[] newArray = new Double[((double[]) o).length];
                    for (int i = 0; i < newArray.length; i++)
                        newArray[i] = ((double[]) o)[i];
                    o = newArray;
                }
                writeTuple((Object[]) o); //TODO: maybe configure into list?
                return;
            }
        } else if (o instanceof String) {
            writeAtom((String) o); //TODO should we optimize for other types?
            return;
        } else {
            writeMap(o);
            return;
        }
        
        throw new ETFException("Unknown object type "+o.getClass());
    }

    /**
     * This gets the current data in a byte array.
     *
     * @return The byte array representing this data.
     */
    public byte[] toBytes() {
        return Arrays.copyOfRange(data, 0, offset);
    }
    
    /**
     * Gives access to the direct buffer in the writer.
     * 
     * @return The underlying buffer.
     */
    public ByteBuffer toBuffer() {
        return ByteBuffer.wrap(toBytes());
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        for (int i = 0; i < offset; i++) {
            builder.append(data[i]);
            if (i+1 != offset)
                builder.append(", ");
        }
        builder.append(">");
        
        return builder.toString();
    }
}
