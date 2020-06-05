package com.austinv11.etf;

import net.openhft.chronicle.bytes.Bytes;

import static com.austinv11.etf.Terms.*;

/**
 * A class which manages a buffer of ETF data.
 */
public class EtfBuffer {

    private static final int INITIAL_BYTE_CAPACITY = 64;

    private final Bytes<?> buffer;

    private EtfBuffer(Bytes<?> buffer) {
        this.buffer = buffer;
    }

    /**
     * Builds a ETF buffer backed by a {@link java.nio.ByteBuffer}
     * @return The buffer.
     */
    public static EtfBuffer onHeapByteBuffer() {
        return new EtfBuffer(Bytes.elasticHeapByteBuffer(INITIAL_BYTE_CAPACITY));
    }

    /**
     * Builds a ETF buffer backed by a {@link java.nio.DirectByteBuffer}.
     * @return The buffer.
     */
    public static EtfBuffer directByteBuffer() {
        return new EtfBuffer(Bytes.elasticByteBuffer(INITIAL_BYTE_CAPACITY));
    }

    /**
     * Builds a ETF buffer which maps off-heap direct memory.
     * @return The buffer.
     */
    public static EtfBuffer direct() {
        return new EtfBuffer(Bytes.allocateElasticDirect(INITIAL_BYTE_CAPACITY));
    }

    /**
     * Builds a ETF buffer with a fixed capacity mapped to off-heap direct memory.
     * @param bytes The number of bytes to allocate.
     * @return The buffer.
     */
    public static EtfBuffer staticDirect(int bytes) {
        return new EtfBuffer(Bytes.allocateDirect(bytes));
    }

    /**
     * Starts writing to the buffer. This isn't explicitly required, but ETF parsers may not understand the output
     * unless you explicitly start writing.
     * @return The buffer for chaining.
     */
    public EtfBuffer startWrite() {
        buffer.writeByte(EtfConstants.VERSION);
        return this;
    }

    public EtfBuffer write()
}
