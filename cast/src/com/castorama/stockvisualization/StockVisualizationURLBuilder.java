/*<ATGCOPYRIGHT>
 * Copyright (C) 2009 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package com.castorama.stockvisualization;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

/**
 * Provides common way to build URL based on set of defined arguments.
 * <p/>
 * Template method {@link #createArgumentsArray()} is intended for
 * obtaining actual array of arguments to format URL. Its behavior is
 * defined by direct sub-type.
 *
 * @author Aliaksandr Belakurski
 */
public abstract class StockVisualizationURLBuilder {
    protected String prodId;
    protected String storeId;
    protected String postalCode;
    protected String channel;
    protected String quantity;
    protected String reduce;
    protected String URLTemplate;

    public StockVisualizationURLBuilder() {
    }

    public StockVisualizationURLBuilder(String pProdId, String pChannel,
                                        String pQquantity, String pURLTemplate) {
        setProdId(pProdId);
        setChannel(pChannel);
        setQuantity(pQquantity);
        setURLTemplate(pURLTemplate);
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getReduce() {
        return reduce;
    }

    public void setReduce(String reduce) {
        this.reduce = reduce;
    }

    public String getURLTemplate() {
        return URLTemplate;
    }

    public void setURLTemplate(String uRLTemplate) {
        URLTemplate = uRLTemplate;
    }

    public URL obtainFormattedURL()
            throws StockVisualizationException {
        Object[] args = createArgumentsArray();

        String formattedURLString;
        try {
            formattedURLString = MessageFormat.format(getURLTemplate(), args);
        } catch (IllegalArgumentException argExc) {
            throw new StockVisualizationException("Unable to obtain URL string for requesting service " +
                    "using the following template " + getURLTemplate() + " and arguments " +
                    args, argExc);
        }

        URL url;
        try {
            url = new URL(formattedURLString);
        } catch (MalformedURLException malfExc) {
            throw new StockVisualizationException("Unable to create URL for requesting service " +
                    "using the following sting representation " + formattedURLString,
                    malfExc);
        }

        return url;
    }

    /**
     * Fills array with arguments to format required URL.
     *
     * @return array of arguments
     */
    abstract protected String[] createArgumentsArray();
}
