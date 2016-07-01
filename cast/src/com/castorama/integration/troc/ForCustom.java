package com.castorama.integration.troc;
import java.io.IOException;

import javax.servlet.ServletException;
import atg.core.util.NumberTable;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class ForCustom extends DynamoServlet {

  public final static String INDEX = "index";
  public final static String COUNTER = "counter";
  public final static String OUTPUT = "output";
  public final static String HOW_MANY = "howMany";


  public void service(DynamoHttpServletRequest pReq, 
                      DynamoHttpServletResponse pRes) 
       throws ServletException, IOException 
  {    
    int howMany = Integer.valueOf(pReq.getParameter(HOW_MANY));
    int counter = Integer.valueOf(pReq.getParameter(COUNTER));

    for (int i = 0; i < howMany; i+=counter) {
      pReq.setParameter(INDEX, NumberTable.getInteger(i));
      pReq.serviceLocalParameter(OUTPUT, pReq, pRes);
    }    
  }
}

