package com.austinv11.etf.erlang;

import com.austinv11.etf.common.TermTypes;
import com.austinv11.etf.util.BertCompatible;
import com.austinv11.etf.util.parsing.ETFParser;

import java.util.AbstractList;
import java.util.List;

//TODO
@BertCompatible
public class ErlangList extends AbstractList<Object> implements ErlangObject {

    private final List<Object> data;
    private final Object tail;
    private final ETFParser parent;

    public ErlangList(List<Object> data, Object tail, ETFParser parent) {
        this.data = data;
        this.tail = tail;
        this.parent = parent;
    }

    @Override
    public byte type() {
        return TermTypes.LIST_EXT;
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    public Object get(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
