package com.austinv11.etf.erlang;

import com.austinv11.etf.common.TermTypes;

//TODO implement
public class Fun implements ErlangObject {

    private final byte[] data;

    public Fun(byte[] data) {
        this.data = data;
    }

    @Override
    public byte type() {
        return TermTypes.FUN_EXT; //TODO differentiate between new and old fun & exports
    }

    @Override
    public byte[] toBytes() {
        return data;
    }
}
