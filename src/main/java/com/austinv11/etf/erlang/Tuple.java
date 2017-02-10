package com.austinv11.etf.erlang;

import com.austinv11.etf.common.TermTypes;
import com.austinv11.etf.util.BertCompatible;
import com.austinv11.etf.util.parsing.ETFParser;

@BertCompatible
public class Tuple implements ErlangObject {
//TODO
    private final Object[] data;
    private final ETFParser parent;

    public Tuple(Object[] data, ETFParser parent) {
        this.data = data;
        this.parent = parent;
    }

    public boolean isSmall() {
        return data.length <= 255;
    }

    @Override
    public byte type() {
        return isSmall() ? TermTypes.SMALL_TUPLE_EXT : TermTypes.LARGE_TUPLE_EXT;
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }
}
