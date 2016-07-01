package com.castorama.seo;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author EPAM team
 */
public class MetaParameter {
    /** DEF_SEPARATOR constant. */
    private static final char DEF_SEPARATOR = ' ';

    /** param property. */
    private String mParam;

    /** separator property. */
    private char mSeparator = DEF_SEPARATOR;

    /**
     * Creates a new MetaParameter object.
     *
     * @param str parameter
     */
    public MetaParameter(String str) {
        mParam = str;
    }

    /**
     * Returns separator property.
     *
     * @return separator property.
     */
    public char getSeparator() {
        return mSeparator;
    }

    /**
     * Sets the value of the separator property.
     *
     * @param pSeparator parameter to set.
     */
    public void setSeparator(char pSeparator) {
        mSeparator = pSeparator;
    }

    /**
     * Returns param property.
     *
     * @return param property.
     */
    public String getParam() {
        return mParam;
    }
}
