package com.austinv11.etf.erlang;

import com.austinv11.etf.common.TermTypes;

//TODO
public class Reference implements ErlangObject {

    private final String atom;
    private final int atomCacheRef;
    private final long id;
    private final byte creation;
    private final long[] newID;

    public Reference(String atom, long id, byte creation) {
        this.atom = atom;
        this.id = id;
        this.creation = creation;
        this.atomCacheRef = -1;
        this.newID = null;
    }

    public Reference(int atomCacheRef, long id, byte creation) {
        this.atomCacheRef = atomCacheRef;
        this.id = id;
        this.creation = creation;
        this.atom = null;
        this.newID = null;
    }

    public Reference(String atom, byte creation, long[] newID) {
        this.atom = atom;
        this.id = -1;
        this.creation = creation;
        this.atomCacheRef = -1;
        this.newID = newID;
    }

    public Reference(int atomCacheRef, byte creation, long[] newID) {
        this.atomCacheRef = atomCacheRef;
        this.id = -1;
        this.creation = creation;
        this.atom = null;
        this.newID = newID;
    }

    public boolean isNew() {
        return newID != null;
    }

    @Override
    public byte type() {
        return isNew() ? TermTypes.NEW_REFERENCE_EXT : TermTypes.REFERENCE_EXT;
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }
}
