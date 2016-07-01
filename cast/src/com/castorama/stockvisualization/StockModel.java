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

import java.io.Serializable;

import atg.repository.RepositoryItem;

/**
 * Entity that encapsulates relevant stock
 * properties.
 *
 * @author Aliaksandr Belakurski
 */
public class StockModel implements Serializable {
    static final String MAGASIN_NAME_PROPERTY_NAME = "nom";
    private String mProdId;
    private String mStoreId;
    private String mStatus;
    private String mStockAvailable;
    private RepositoryItem magasin;
    private String quantity;

    public StockModel() {
    }

    public StockModel(String pProdId, String pStoreId, String pStatus,
                      String pStockAvailable, RepositoryItem pMagasin) {
        mProdId = pProdId;
        mStoreId = pStoreId;
        mStatus = pStatus;
        mStockAvailable = pStockAvailable;
        this.magasin = pMagasin;
    }

    public String getProdId() {
        return mProdId;
    }

    public void setProdId(String pProdId) {
        mProdId = pProdId;
    }

    public String getStoreId() {
        return mStoreId;
    }

    public void setStoreId(String pStoreId) {
        mStoreId = pStoreId;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String pStatus) {
        mStatus = pStatus;
    }

    public String getStockAvailable() {
        return mStockAvailable;
    }

    public void setStockAvailable(String pStockAvailable) {
        mStockAvailable = pStockAvailable;
    }

    public RepositoryItem getMagasin() {
        return magasin;
    }

    public void setMagasin(RepositoryItem magasin) {
        this.magasin = magasin;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMagasinName() {
        return (String) magasin.getPropertyValue(MAGASIN_NAME_PROPERTY_NAME);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("prodId: ").append(getProdId()).append(", ");
        sb.append("storeId: ").append(getStoreId()).append(", ");
        sb.append("status: ").append(getStatus()).append(", ");
        sb.append("stockAvailable: ").append(getStockAvailable()).append(", ");
        sb.append("magasinId: ").append(magasin.getRepositoryId());

        return sb.toString();
    }
}
