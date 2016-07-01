/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */

package com.castorama.droplet;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.utils.CastCipherHelper;
import com.castorama.utils.HexEncoder;

import java.io.IOException;

import javax.servlet.ServletException;


/**
 * Droplet to encode 'Print Ticket PDF' parameters;
 * See Mantis 0001973: [Purchase History] PDF Ticket URL
 *
 * @author Aliaksandr Surma
  */
public class TicketPDFSecureTokenDroplet extends DynamoServlet {
  //--------------------------------------
  // Constants
  private static final String SEPARATOR = ":";

  //input parameters
  protected static final String USER_ID = "userId";
  protected static final String MODE = "mode";
  protected static final String TICKET_ID = "ticketId";
  protected static final String ENCODE = "encode";

  //open parameters
  protected static final String OPARAM_OUTPUT = "output";
  protected static final String ERRORPARAM_OUTPUT = "error";

  //output parameters
  protected static final String ENCODED_PARAM = "encodedParam";

  //-------------------------------------
  // Properties

  //-------------------------------------
  // property: cipherHelper
  private CastCipherHelper mCipherHelper;

  /**
   * Gets the cipherHelper property.
   *
   * @return the cipherHelper
   */
  public final CastCipherHelper getCipherHelper() {
    return mCipherHelper;
  }

  /**
   * Sets the cipherHelper property.
   *
   * @param pCipherHelper the cipherHelper to set
   */
  public final void setCipherHelper(final CastCipherHelper pCipherHelper) {
    mCipherHelper = pCipherHelper;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pRequest ToDo: DOCUMENT ME!
   * @param pResponse ToDo: DOCUMENT ME!
   *
   * @throws ServletException ToDo: DOCUMENT ME!
   * @throws IOException ToDo: DOCUMENT ME!
   */
  public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    final String mode = pRequest.getParameter(MODE);

    if(mode == null) {
      if(isLoggingDebug()) {
        logDebug("MISSING PARAM: no mode supplied");
      }

      return;
    }

    if(mode.equals("encode")) {
      doEncode(pRequest, pResponse);
    } else {
      doDecode(pRequest, pResponse);
    }
  }

  /**
   * Decodes and parses secure token
   *
   * @param pRequest ToDo: DOCUMENT ME!
   * @param pResponse ToDo: DOCUMENT ME!
   *
   * @throws ServletException ToDo: DOCUMENT ME!
   * @throws IOException ToDo: DOCUMENT ME!
   */
  protected void doDecode(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   * Builds and encodes secure token
   *
   * @param pRequest ToDo: DOCUMENT ME!
   * @param pResponse ToDo: DOCUMENT ME!
   *
   * @throws ServletException ToDo: DOCUMENT ME!
   * @throws IOException ToDo: DOCUMENT ME!
   */
  protected void doEncode(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    final String userId = pRequest.getParameter(USER_ID);

    if(userId == null) {
      if(isLoggingDebug()) {
        logDebug("MISSING PARAM: no userId supplied");
      }

      return;
    }

    final String ticketId = pRequest.getParameter(TICKET_ID);

    if(ticketId == null) {
      if(isLoggingDebug()) {
        logDebug("INCORRECT PARAM: no ticketId supplied");
      }

      return;
    }

    final String paramToEncode = ticketId + SEPARATOR + userId;

    try {
      final HexEncoder hencoder = new HexEncoder();

      byte[] encodedParam = getCipherHelper().encrypt(paramToEncode.getBytes());
      pRequest.setParameter(ENCODED_PARAM, hencoder.encode(encodedParam));
      pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
    } catch(Exception e) {
      if(isLoggingError()) {
        logError(e);
      }

      pRequest.serviceLocalParameter(ERRORPARAM_OUTPUT, pRequest, pResponse);
    }
  }
}
