/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */
package com.castorama.purchasehistory.pdf;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.servlet.GenericServletService;

import com.castorama.commerce.order.invoice.PrintPdfHelper;

import com.castorama.commerce.profile.Constants;

import com.castorama.purchasehistory.TicketRetrievalException;
import com.castorama.purchasehistory.TicketRetrievalService;

import com.castorama.utils.CastCipherHelper;
import com.castorama.utils.HexEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.xml.transform.TransformerException;


/**
 * Servlet class for "Print Ticket" functionality.
 *
 * @author Aliaksandr Surma
  */
public class PrintTicketPdfServlet extends GenericServletService {
  /** Parameter's separator. */
  private static final String SEPARATOR = ":";

  /** Holds the Ticket's id. */
  private static final String ENCODED_PARAM = "t";

  /** Holds the Ticket's id parameter key. */
  private static final String TICKET_ID_PARAM_KEY = "ticket_id";

  /** Holds the User's id parameter key. */
  private static final String USER_ID_PARAM_KEY = "user_id";

  /** Holds the CONTENT_TYPE_PDF. */
  public static final String CONTENT_TYPE_PDF = "application/pdf";
  private Repository mProfileRepository;

  /**
   * Gets the profileRepository property.
   *
   * @return the profileRepository
   */
  public final Repository getProfileRepository() {
    return mProfileRepository;
  }

  /**
   * Sets the profileRepository property.
   *
   * @param pProfileRepository the profileRepository to set
   */
  public final void setProfileRepository(final Repository pProfileRepository) {
    mProfileRepository = pProfileRepository;
  }

  private TicketRetrievalService mTicketRetrievalService;

  /**
   * Gets the ticketRetrievalService property.
   *
   * @return the ticketRetrievalService
   */
  public final TicketRetrievalService getTicketRetrievalService() {
    return mTicketRetrievalService;
  }

  /**
   * Sets the ticketsRetrievalService property.
   *
   * @param pTicketRetrievalService the ticketRetrievalService to set
   */
  public final void setTicketRetrievalService(final TicketRetrievalService pTicketRetrievalService) {
    mTicketRetrievalService = pTicketRetrievalService;
  }

  private CastCipherHelper cipherHelper;

  /**
   * Gets the cipherHelper property. 
   *
   * @return the cipherHelper
   */
  public final CastCipherHelper getCipherHelper() {
    return cipherHelper;
  }

  /**
   * Sets the cipherHelper property.
   *
   * @param pCipherHelper the cipherHelper to set
   */
  public final void setCipherHelper(final CastCipherHelper pCipherHelper) {
    cipherHelper = pCipherHelper;
  }

  /**
   * Service method to handle "Print Ticket" actions.
   *
   * @param  pServletRequest  parameter
   * @param  pServletResponse parameter
   *
   * @throws IOException      exception
   * @throws ServletException exception
   */
  public void handleService(final ServletRequest pServletRequest, final ServletResponse pServletResponse)
    throws IOException, ServletException {
    String ticketRepoId = null;
    String userId = null;

    // All parameters are secured
    // See Mantis 0001973: [Purchase History] PDF Ticket URL
    boolean generate = true;
    final String encodedParam = pServletRequest.getParameter(ENCODED_PARAM);

    final ByteArrayOutputStream pdf = new ByteArrayOutputStream();

    Map parameters = parseEncodedParams(encodedParam);

    if(parameters != null) {
      ticketRepoId = (String) parameters.get(TICKET_ID_PARAM_KEY);
      userId = (String) parameters.get(USER_ID_PARAM_KEY);
    } else {
      generate = false;
    }

    final RepositoryItem user = findUserById(userId);

    if(user == null) {
      if(isLoggingError()) {
        logError("User with id=" + userId + " was not found.");
      }

      generate = false;
    }

    RepositoryItem ticket = null;
    RepositoryItem store = null;

    try {
      ticket = mTicketRetrievalService.getTicketByRepositoryId(ticketRepoId);
      store = mTicketRetrievalService.getStoreByTicket(ticket);
    } catch(TicketRetrievalException tre) {
      if(isLoggingError()) {
        logError(tre);
      }

      generate = false;
    }

    if(ticket == null) {
      if(isLoggingError()) {
        logError("Ticket with id=" + ticketRepoId + " was not found.");
      }

      generate = false;
    }

    if(store == null && ticket != null) {
      final String storeId = (String) ticket.getPropertyValue("storeId");

      if(isLoggingError()) {
        logError("Store for ticket[" + ticketRepoId + "] was not found. StoreId=" + storeId);
      }

      //Allow to generate PDF with empty STORE section
      //return;
    }

    final Map params = new HashMap();
    params.put("url",
      pServletRequest.getScheme() + "://" + pServletRequest.getServerName() + ":" + pServletRequest.getServerPort());

    try {
      
      if (generate) {
        PrintPdfHelper.getInstance().generateTicketPdf(pdf, ticket, store, user, mTicketRetrievalService, params);
      } else {
        PrintPdfHelper.getInstance().generateTicketErrorPdf(pdf, params);
      }

      // Set header
      pServletResponse.setContentType(CONTENT_TYPE_PDF);

      pServletResponse.setContentLength(pdf.size());

      final OutputStream out = pServletResponse.getOutputStream();
      pdf.writeTo(out);
      out.flush();
      out.close();
    } catch(IOException e) {
      logError(e);
    } catch(TransformerException e) {
      logError(e);
    } // end try-catch
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pEncodedParam ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  private Map parseEncodedParams(final String pEncodedParam) {
    Map result = null;
    final HexEncoder hencoder = new HexEncoder();

    try {
      final String decodedParam = getCipherHelper().decrypt(new String(hencoder.decode(pEncodedParam)));
      final StringTokenizer st = new StringTokenizer(decodedParam, SEPARATOR);

      if(st.countTokens() == 2) {
        final String ticketId = st.nextToken();
        final String userId = st.nextToken();
        result = new HashMap();
        result.put(TICKET_ID_PARAM_KEY, ticketId);
        result.put(USER_ID_PARAM_KEY, userId);
      }
    } catch(IllegalBlockSizeException e) {
    } catch(BadPaddingException e) {
    } catch(IOException e) {
    } catch (ArrayIndexOutOfBoundsException e) {
    }

    return result;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pUserId ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  private RepositoryItem findUserById(final String pUserId) {
    RepositoryItem user = null;

    if(pUserId != null) {
      final Repository profileRepository = getProfileRepository();

      try {
        user = profileRepository.getItem(pUserId, Constants.DESCRIPTOR_NAME_PROFILE);
      } catch(RepositoryException e) {
        if(isLoggingError()) {
          logError(e);
        }
      }
    } else {
      if(isLoggingError()) {
        logError("User id is null");
      }
    }

    return user;
  }
}
