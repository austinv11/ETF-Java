package com.austinv11.etf;

import com.austinv11.etf.parsing.ETFParser;
import com.austinv11.etf.util.ETFConstants;
import com.austinv11.etf.util.Mapper;
import com.austinv11.etf.writing.ETFWriter;

/**
 * This provides a clean way to configure etf handlers.
 */
public class ETFConfig {

    private boolean bert = false;
    private int version = ETFConstants.VERSION;
    private boolean includeHeader = true;
    private boolean includeDistributionHeader = false;
    private boolean loqui = false;
    private boolean compress = false;

    /**
     * This returns whether this supports <a href="http://bert-rpc.org/">BERT</a>.
     *
     * @return True if BERT is configured, false if otherwise.
     */
    public boolean isBert() {
        return bert;
    }

    /**
     * This sets whether this handles <a href="http://bert-rpc.org/">BERT</a>.
     *
     * @param bert Whether to support BERT.
     * @return The current config instance (for chaining).
     *
     * @see com.austinv11.etf.util.BertCompatible
     * @see com.austinv11.etf.util.BertType
     */
    public ETFConfig setBert(boolean bert) {
        this.bert = bert;
        return this;
    }

    /**
     * This gets the version of ETF this is handling.
     *
     * @return The version.
     */
    public int getVersion() {
        return version;
    }

    /**
     * This sets the specific version of etf this is handling.
     * WARNING: No matter what version you set, the internal handlers will only respect {@link ETFConstants#VERSION}'s
     * spec.
     *
     * @param version The version integer.
     * @return The current config instance (for chaining).
     */
    public ETFConfig setVersion(int version) {
        this.version = version;
        return this;
    }

    /**
     * This returns whether headers are included.
     * NOTE This is NOT a distribution header setting.
     *
     * @return True if including a header.
     */
    public boolean isIncludingHeader() {
        return includeHeader;
    }

    /**
     * Sets whether this will include a header.
     * NOTE This is NOT a distribution header setting.
     *
     * @param includeHeader Set to true to include a header.
     * @return The current config instance (for chaining).
     */
    public ETFConfig setIncludeHeader(boolean includeHeader) {
        this.includeHeader = includeHeader;
        return this;
    }

    /**
     * This returns whether this config supports the <a href="https://github.com/hammerandchisel/loqui">Loqui</a>
     * (Discord's) transport protocol.
     *
     * @return True if loqui handling is enabled, false if otherwise.
     */
    public boolean isLoqui() {
        return loqui;
    }

    /**
     * Sets whether this will have special handling for <a href="https://github.com/hammerandchisel/loqui">Loqui</a>,
     * Discord's transport protocol.
     *
     * @param loqui Set to true to handle loqui types, false to ignore them.
     * @return The current config instance (for chaining).
     */
    public ETFConfig setLoqui(boolean loqui) {
        this.loqui = loqui;
        return this;
    }

    /**
     * This returns whether distribution headers are written.
     *
     * @return True when enabled, false when otherwise.
     */
    public boolean isIncludingDistributionHeader() {
        return includeDistributionHeader; //TODO
    }

    /**
     * This sets whether distribution headers are written.
     *
     * @param includeDistributionHeader Set to true to write distribution headers, false to not.
     * @return The current config instance (for chaining).
     */
    public ETFConfig setIncludeDistributionHeader(boolean includeDistributionHeader) {
        this.includeDistributionHeader = includeDistributionHeader;
        return this;
    }

    /**
     * This gets whether ETF should be compressed when written.
     *
     * @return True when etf is compressed when written.
     */
    public boolean isCompressing() {
        return compress;
    }

    /**
     * This sets whether ETF should be compressed when written.
     *
     * @param compress Set to true to compress, false to not.
     * @return The current config instance (for chaining).
     */
    public ETFConfig setCompression(boolean compress) {
        this.compress = compress;
        return this;
    }

    /**
     * This creates a new parser using the set configuration.
     *
     * @param data The data to parse.
     * @return The new parser instance.
     */
    public ETFParser createParser(byte[] data) {
        return createParser(data, false);
    }

    /**
     * This creates a new parser using the set configuration.
     *
     * @param data The data to parse.
     * @param partial Whether the data should be treated as partial (meaning no headers).
     * @return The new parser instance.
     */
    public ETFParser createParser(byte[] data, boolean partial) {
        return new ETFParser(data, this, partial);
    }

    /**
     * This creates a new writer using the set configuration.
     *
     * @return The new writer instance.
     */
    public ETFWriter createWriter() {
        return createWriter(false);
    }

    /**
     * This creates a new writer using the set configuration.
     *
     * @param partial Whether the data should be treated as partial (meaning no headers).
     * @return The new writer instance.
     */
    public ETFWriter createWriter(boolean partial) {
        return new ETFWriter(this, partial);
    }
    
    /**
     * This creates a new mapper using this configuration.
     *
     * @return The new mapper instance.
     */
    public Mapper createMapper() {
        return new Mapper(this);
    }
}
