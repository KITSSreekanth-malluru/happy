package com.castorama.integration.ticket;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TicketHeaderObject {

  private final List<TicketMethodObject> methods = new LinkedList<TicketMethodObject>();
  private final List<TicketLineObject> lines = new LinkedList<TicketLineObject>();
  private String wdmId;
  private String ticketId;
  private String storeId;
  private Date ticketDate;
  private String homeId;
  private String tillId;
  private String hostessId;
  private double totalQuantity;
  private double totalPriceTTC;
  private double totalHT;
  
  public void clear() {
    wdmId = null;
    ticketId = null;
    storeId = null;
    ticketDate = null;
    homeId = null;
    tillId = null;
    hostessId = null;
    methods.clear();
    lines.clear();
  }
  
  @Override
  public String toString() {
    StringBuffer result = new StringBuffer(255);
    result.append("TICKET HEADER[");
    result.append("wdmId=").append(wdmId);
    result.append(",ticketId=").append(ticketId);
    result.append(",storeId=").append(storeId);
    result.append(",ticketDate=").append(ticketDate);
    result.append(",homeId=").append(homeId);
    result.append(",tillId=").append(tillId);
    result.append(",hostessId=").append(hostessId);
    result.append(",totalQuantity=").append(totalQuantity);
    result.append(",totalPriceTTC=").append(totalPriceTTC);
    result.append(",totalHT=").append(totalHT);
    for ( int index = 0; index < methods.size(); index++ ) {
      result.append("method").append(index).append("=").append(methods.get(index));
    }
    for ( int index = 0; index < lines.size(); index++ ) {
      result.append("line").append(index).append("=").append(lines.get(index));
    }
    result.append("]");
    return result.toString();
  }

  /**
   * @return the wdmId
   */
  public String getWdmId() {
    return wdmId;
  }

  /**
   * @param wdmId the wdmId to set
   */
  public void setWdmId(String wdmId) {
    this.wdmId = wdmId;
  }

  /**
   * @return the ticketId
   */
  public String getTicketId() {
    return ticketId;
  }

  /**
   * @param ticketId the ticketId to set
   */
  public void setTicketId(String ticketId) {
    this.ticketId = ticketId;
  }

  /**
   * @return the storeId
   */
  public String getStoreId() {
    return storeId;
  }

  /**
   * @param storeId the storeId to set
   */
  public void setStoreId(String storeId) {
    this.storeId = storeId;
  }

  /**
   * @return the ticketDate
   */
  public Date getTicketDate() {
    return ticketDate;
  }

  /**
   * @param ticketDate the ticketDate to set
   */
  public void setTicketDate(Date ticketDate) {
    this.ticketDate = ticketDate;
  }

  /**
   * @return the homeId
   */
  public String getHomeId() {
    return homeId;
  }

  /**
   * @param homeId the homeId to set
   */
  public void setHomeId(String homeId) {
    this.homeId = homeId;
  }

  /**
   * @return the tillId
   */
  public String getTillId() {
    return tillId;
  }

  /**
   * @param tillId the tillId to set
   */
  public void setTillId(String tillId) {
    this.tillId = tillId;
  }

  /**
   * @return the hostessId
   */
  public String getHostessId() {
    return hostessId;
  }

  /**
   * @param hostessId the hostessId to set
   */
  public void setHostessId(String hostessId) {
    this.hostessId = hostessId;
  }

  /**
   * @return the totalQuantity
   */
  public double getTotalQuantity() {
    return totalQuantity;
  }

  /**
   * @param totalQuantity the totalQuantity to set
   */
  public void setTotalQuantity(double totalQuantity) {
    this.totalQuantity = totalQuantity;
  }

  /**
   * @return the totalPriceTTC
   */
  public double getTotalPriceTTC() {
    return totalPriceTTC;
  }

  /**
   * @param totalPriceTTC the totalPriceTTC to set
   */
  public void setTotalPriceTTC(double totalPriceTTC) {
    this.totalPriceTTC = totalPriceTTC;
  }

  /**
   * @return the totalHT
   */
  public double getTotalHT() {
    return totalHT;
  }

  /**
   * @param totalHT the totalHT to set
   */
  public void setTotalHT(double totalHT) {
    this.totalHT = totalHT;
  }

  /**
   * @return the methods
   */
  public List<TicketMethodObject> getMethods() {
    return methods;
  }

  /**
   * @return the lines
   */
  public List<TicketLineObject> getLines() {
    return lines;
  }

}
