package com.austinv11.etf.erlang;

import com.austinv11.etf.common.TermTypes;

//TODO
public class PID implements ErlangObject {

    private final String atom;
    private final int atomCacheRef;
    private final int id;
    private final int serial;
    private final byte creation;

    public PID(String atom, int id, int serial, byte creation) {
        this.atom = atom;
        this.id = id;
        this.serial = serial;
        this.creation = creation;
        this.atomCacheRef = -1;
    }

    public PID(int atomCacheRef, int id, int serial, byte creation) {
        this.atomCacheRef = atomCacheRef;
        this.id = id;
        this.serial = serial;
        this.creation = creation;
        this.atom = null;
    }

    @Override
    public byte type() {
        return TermTypes.PID_EXT;
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }
}
