package com.castorama.utils;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import org.xml.sax.ext.LexicalHandler;

import org.xml.sax.helpers.XMLFilterImpl;

/**
 * <code>SaxXmlBuilder</code> is a basic XML Builder implementation providing
 * support for some SAX2 events.
 *
 * @version  1.0.
 */
public class SaxXmlBuilder extends XMLFilterImpl {
    /** SAX2 property key identifying <code>LexicalHandler</code> support. */
    public static final String PROPERTY_LEXICAL_HANDLER =
        "http://xml.org/sax/properties/lexical-handler";

    /** holds attribute type <code>CDATA</code> value. */
    public static final String CDATA = "CDATA";

    /** SAX2 Lexical Handler instance. */
    private LexicalHandler lexHandler;

    /**
     * Sets custom SAX property. Only Lexical Handler
     * (http://xml.org/sax/properties/lexical-handler) is supported.
     *
     * @param   message  property key
     * @param   value    property value
     *
     * @throws  SAXNotRecognizedException  if provided property is not supported
     * @throws  SAXNotSupportedException   if provided property is not supported
     */
    public void setProperty(final String message, final Object value)
                     throws SAXNotRecognizedException, SAXNotSupportedException {
        if (PROPERTY_LEXICAL_HANDLER.equals(message) &&
                (value instanceof LexicalHandler)) {
            lexHandler = (LexicalHandler) value;
        } else {
            super.setProperty(message, value);
        }
    }

    /**
     * Returns the SAX property value identified by the given key. Only Lexical
     * Handler (http://xml.org/sax/properties/lexical-handler) is supported.
     *
     * @param   message  property key
     *
     * @return  property value
     *
     * @throws  SAXNotRecognizedException  if request property is not supported
     * @throws  SAXNotSupportedException   if request property is not supported
     */
    public Object getProperty(final String message) throws SAXNotRecognizedException,
                                                           SAXNotSupportedException {
        if (PROPERTY_LEXICAL_HANDLER.equals(message)) {
            return lexHandler;
        } else {
            return super.getProperty(message);
        }
    }

    /**
     * Starts <code>CDATA</code> section marker. Delegates this call to the
     * <code>LexicalHandler</code> instance if such defined. If <code>
     * LexicalHandler</code> not defined just does nothing.
     *
     * @throws  SAXException  if thrown by underlying <code>
     *                        LexicalHandler</code> implementation
     */
    public void startCDATA() throws SAXException {
        if (null != lexHandler) {
            lexHandler.startCDATA();
        }
    }

    /**
     * Ends <code>CDATA</code> section marker. Delegates this call to the <code>
     * LexicalHandler</code> instance if such defined. If <code>
     * LexicalHandler</code> not defined just does nothing.
     *
     * @throws  SAXException  if thrown by underlying <code>
     *                        LexicalHandler</code> implementation
     */
    public void endCDATA() throws SAXException {
        if (null != lexHandler) {
            lexHandler.endCDATA();
        }
    }
}
