package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 *
 * @author EPAM team
 */
public class ProductDescPreProcessorDroplet extends DynamoServlet {
    /** PARAMETER_SHOW_SEE_MORE constant. */
    private static final String PARAMETER_SHOW_SEE_MORE = "showSeeMore";

    /** PARAMETER_RESULT constant. */
    private static final String PARAMETER_RESULT = "result";

    /** maxBulletNumber property. 
     * NOT USED ANYMORE
     * @deprecated 
     */
    @Deprecated
    private int mMaxBulletNumber = 5;

    /** maxTextLength property. */
    private int mMaxTextLength = 300;
    
    /**
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        String descr = pRequest.getParameter("description");
        String sessionId = pRequest.getSession(false) != null ? pRequest.getSession().getId() : "NOSESSIONID";
        if(isLoggingDebug()){
            logDebug(sessionId+":Start processing description: \"" + descr + "\".");
        }

        if ((descr != null) && (descr.length()>0)) {
            if(isLoggingDebug()) {
                logDebug(sessionId+":Start computing description.");
            }
            
            ProductDescriptionDisplayerProcessor processor = new ProductDescriptionDisplayerProcessor(getMaxTextLength());
            long start = System.currentTimeMillis();
            String result = processor.compute(descr);
            long processingTime = System.currentTimeMillis() - start;
            if(isLoggingDebug()){
                logDebug(sessionId+":Finish computing description. Result: \"" + result + "\". Computing lasts : " + processingTime + " milliseconds.");
            }

            pRequest.setParameter(PARAMETER_RESULT, result);
            boolean showSeeMoreProperty=false;
            if(descr.length() > result.length()){
                showSeeMoreProperty=true;
            }
            pRequest.setParameter(PARAMETER_SHOW_SEE_MORE, showSeeMoreProperty);             
        } 
        pRequest.serviceLocalParameter("output", pRequest, pResponse);
        
        if(isLoggingDebug()){
            logDebug(sessionId+":Finish of processing description: \"" + descr + "\".");
        }
    }
    
    /**
     * Returns maxBulletNumber property.
     *
     * @return maxBulletNumber property.
     */
    public int getMaxBulletNumber() {
        return mMaxBulletNumber;
    }

    /**
     * Sets the value of the maxBulletNumber property.
     *
     * @param pMaxBulletNumber parameter to set.
     */
    public void setMaxBulletNumber(int pMaxBulletNumber) {
        mMaxBulletNumber = pMaxBulletNumber;
    }

    /**
     * Returns maxTextLength property.
     *
     * @return maxTextLength property.
     */
    public int getMaxTextLength() {
        return mMaxTextLength;
    }

    /**
     * Sets the value of the maxTextLength property.
     *
     * @param pMaxTextLength parameter to set.
     */
    public void setMaxTextLength(int pMaxTextLength) {
        mMaxTextLength = pMaxTextLength;
    }

}
