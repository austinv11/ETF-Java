package com.austinv11.etf.erlang;

import com.austinv11.etf.common.TermTypes;

public class Port implements ErlangObject {

    private final String atom;
    private final int atomCacheRef;
    private final int id;
    private final byte creation;

    public Port(String atom, int id, byte creation) {
        this.atom = atom;
        this.id = id;
        this.creation = creation;
        this.atomCacheRef = -1;
    }

    public Port(int atomCacheRef, int id, byte creation) {
        this.atomCacheRef = atomCacheRef;
        this.id = id;
        this.creation = creation;
        this.atom = null;
    }

    @Override
    public byte type() {
        return TermTypes.PORT_EXT;
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }
}
