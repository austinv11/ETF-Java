package com.austinv11.etf.erlang;

import com.austinv11.etf.common.TermTypes;
import com.austinv11.etf.util.BertCompatible;
import com.austinv11.etf.util.parsing.ETFParser;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//TODO
@BertCompatible
public class ErlangMap extends AbstractMap<Object, Object> implements ErlangObject {

    private final Map<Object, Object> data;
    private final ETFParser parent;

    public ErlangMap(Map<Object, Object> data, ETFParser parent) {
        this.data = data;
        this.parent = parent;
    }

    @Override
    public byte type() {
        return TermTypes.MAP_EXT;
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return data.entrySet();
    }
}
