package com.castorama.integration.ticket;

public class TicketMethodObject {

  private int paymentType;
  private double paymentAmmount;

  @Override
  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append("TICKET METHOD[");
    result.append("paymentType=").append(paymentType);
    result.append(",paymentAmmount=").append(paymentAmmount);
    result.append("]");
    return result.toString();
  }

  /**
   * @return the paymentType
   */
  public int getPaymentType() {
    return paymentType;
  }

  /**
   * @param paymentType the paymentType to set
   */
  public void setPaymentType(int paymentType) {
    this.paymentType = paymentType;
  }

  /**
   * @return the paymentAmmount
   */
  public double getPaymentAmmount() {
    return paymentAmmount;
  }

  /**
   * @param paymentAmmount the paymentAmmount to set
   */
  public void setPaymentAmmount(double paymentAmmount) {
    this.paymentAmmount = paymentAmmount;
  }

}
