package com.castorama.integration.ticket;

public interface ParseTicketable {

  public static final int START = 0;

  public static final int MAPPING = 1;

  public static final int HEADER = 2;
  
  public static final String PARSE_DELIMITER = ";";

  public void addLine(String line);

  public void finish();

}
