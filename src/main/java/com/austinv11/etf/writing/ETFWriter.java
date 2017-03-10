package com.austinv11.etf.writing;

import com.austinv11.etf.ETFConfig;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.austinv11.etf.common.TermTypes.*;

/**
 * This represents a utility writer for writing data to an etf object.
 */
public class ETFWriter {

    private ByteBuffer buffer = ByteBuffer.allocateDirect(64).order(ByteOrder.BIG_ENDIAN);
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
        if (buffer.remaining() < data.length+1/*Ensure room for a version byte if necessary*/) { //We need to expand the buffer
            byte[] current = buffer.array();
            buffer = ByteBuffer.allocateDirect(buffer.capacity()*2).order(ByteOrder.BIG_ENDIAN);
            buffer.put(current);
        }

        if (data[0] != version && !includeDistributionHeader)
            buffer.put(version);

        buffer.put(data);
    }

    public ETFWriter writeAtomCacheIndex(short index) {
        writeToBuffer(ATOM_CACHE_REF, (byte) index);
        return this;
    }

    public ETFWriter writeSmallInteger(short integer) {
        writeToBuffer(ATOM_CACHE_REF, (byte) integer);
        return this;
    }

    public ETFWriter writeLargeInteger(int integer) {

    }

    public ETFWriter writeInteger(int integer) {

    }

    /**
     * This writes a supported object to ETF.
     *
     * @param o The object to write.
     *
     * @throws com.austinv11.etf.util.ETFException When the object isn't supported.
     */
    public void write(Object o) {
        //TODO
    }

    /**
     * This gets the current data in a byte array.
     *
     * @return The byte array representing this data.
     */
    public byte[] toBytes() {
        return buffer.array();
    }
}
