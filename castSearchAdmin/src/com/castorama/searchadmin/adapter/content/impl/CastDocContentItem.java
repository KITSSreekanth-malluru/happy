package com.castorama.searchadmin.adapter.content.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import atg.searchadmin.adapter.content.impl.BasicContentItem;

/**
 * Extend for supporting CastoramaDocument objects
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastDocContentItem extends BasicContentItem {
    /** castDocument property */
    private byte[] mCastDocument;

    /**
     * Constructor
     *
     * @param pCastDocument parameter
     */
    public CastDocContentItem(byte[] pCastDocument) {
        mCastDocument = pCastDocument;
    }

    /**
     * Returns document's size
     *
     * @return size of document
     *
     * @throws IOException
     */
    public long getSize() throws IOException {
        return mCastDocument.length;
    }

    /**
     * Returns charSet property.
     *
     * @return charSet property.
     *
     * @throws IOException - exception
     */
    protected String getCharSet() throws IOException {
        return null;
    }

    /**
     * Returns CastDocument Content item
     *
     * @return CastDocument Content item
     */
    public byte[] getCastDocument() {
        return mCastDocument;
    }

    /**
     * Returns CastDocument content as string
     *
     * @return CastDocument content as string
     *
     * @throws IOException exception
     */
    public String getContentAsString() throws IOException {
        return new String(mCastDocument, 0, mCastDocument.length);
    }

    /**
     * Returns CastDocument content as Reader
     *
     * @return CastDocument content as Reader
     *
     * @throws IOException exception
     */
    public Reader getContentAsReader() throws IOException {
        String strCharSet = getCharSet();
        Reader readerResult;

        if (strCharSet == null) {
            readerResult = new InputStreamReader(new ByteArrayInputStream(mCastDocument));
        } else {
            readerResult = new InputStreamReader(new ByteArrayInputStream(mCastDocument), strCharSet);
        }
        return readerResult;
    }

}
