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
 * Signalizes about exceptional case occurred during execution
 * of stock visualization logic.
 *
 * @author Alisakndr Belakurski
 */
public class StockVisualizationException extends Exception {

    public StockVisualizationException() {
        super();
    }

    /**
     * Constructs a new StockVisualizationException with the given
     * explanation.
     */
    public StockVisualizationException(String pStr) {
        super(pStr);
    }

    /**
     * Constructs a new StockVisualizationException.
     *
     * @param pSourceException the initial exception which was the root
     *                         cause of the problem
     */
    public StockVisualizationException(Throwable pSourceException) {
        super(pSourceException);
    }

    /**
     * Constructs a new StockVisualizationException with the given
     * explanation.
     *
     * @param pSourceException the initial exception which was the root
     *                         cause of the problem
     */
    public StockVisualizationException(String pStr, Throwable pSourceException) {
        super(pStr, pSourceException);
    }

}
