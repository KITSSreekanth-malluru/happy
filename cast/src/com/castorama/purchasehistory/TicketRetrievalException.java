/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */

package com.castorama.purchasehistory;


/**
 * Common exception for "Purchase History" related classes.
 *
 * @author Aliaksandr Surma
  */
public class TicketRetrievalException extends Exception {
  private static final long serialVersionUID = 8943206975826643496L;

  /**
   * Creates a new TicketsRetrievalException object.
   *
   * @param pThrowable chain exception
   */
  public TicketRetrievalException(Throwable pThrowable) {
    super(pThrowable);
  }

  /**
   * Creates a new TicketsRetrievalException object.
   *
   * @param pMessage error message
   */
  public TicketRetrievalException(String pMessage) {
    super(pMessage);
  }
}
