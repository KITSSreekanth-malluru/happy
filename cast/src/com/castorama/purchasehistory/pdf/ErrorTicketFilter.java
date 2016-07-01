/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */
package com.castorama.purchasehistory.pdf;

import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.castorama.utils.SaxXmlBuilder;


/**
 * <code>ErrorTicketFilter</code> is subclass of <code>SaxXmlBuilder</code>. It is
 * used for generation xml with empty data.
 *
 * @author Aliaksandr Surma
 */
public final class ErrorTicketFilter extends SaxXmlBuilder {
 
  //-------------------------------------
  // Elements
  /** <ticket> element. */
  private static final String EL_TICKET = "ticket";

  /**
   * Creates a new ErrorTicketFilter object.
   */
  public ErrorTicketFilter() {
   
  }

  /**
   * Parse a document.
   *
   * @param  pInputSource The input source for the document entity
   *
   * @throws SAXException Any SAX exception, possibly wrapping another
   *                      exception.
   * @throws IOException  An IO exception from the parser, possibly from a
   *                      byte stream or character stream supplied by the
   *                      application
   */
  public void parse(final InputSource pInputSource) throws SAXException, IOException {
    startDocument();
    // empty xml
    startElement("", EL_TICKET, EL_TICKET, new AttributesImpl());
    endElement("", EL_TICKET, EL_TICKET);
    endDocument();
  }
}
