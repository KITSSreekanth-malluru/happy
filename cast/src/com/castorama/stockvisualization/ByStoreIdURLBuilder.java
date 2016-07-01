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


/**
 * Builds URL to poll web service for products stock level
 * in particular store.
 *
 * @author Aliaksandr Belakurski
 */
public class ByStoreIdURLBuilder extends StockVisualizationURLBuilder {
    public ByStoreIdURLBuilder() {
    }

    public ByStoreIdURLBuilder(String pStoreId, String pProdId, String pChannel,
                               String pQuantity, String pReduce, String pURLTemplate) {
        super(pProdId, pChannel, pQuantity, pURLTemplate);
        setStoreId(pStoreId);
        setReduce(pReduce);
    }

    /*
     * (non-Javadoc)
     * @see com.castorama.stockvisualization.StockVisualizationURLBuilder#createArgumentsArray()
     */
    protected String[] createArgumentsArray() {
        return new String[]{getProdId(), getStoreId(), getChannel(), getQuantity(), getReduce()};
    }
}
