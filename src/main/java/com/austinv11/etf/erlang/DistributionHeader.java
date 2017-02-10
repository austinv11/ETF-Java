package com.austinv11.etf.erlang;

import com.austinv11.etf.common.TermTypes;

//TODO implement
public class DistributionHeader implements ErlangObject {

    private final byte[] data;

    public DistributionHeader(byte[] data) {
        this.data = data;
    }

    @Override
    public byte type() {
        return TermTypes.DISTRIBUTION_HEADER;
    }

    @Override
    public byte[] toBytes() {
        return data;
    }
}
