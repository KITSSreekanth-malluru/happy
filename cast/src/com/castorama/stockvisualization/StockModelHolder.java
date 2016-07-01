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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import atg.nucleus.GenericService;

/**
 * Session-scoped component that provides convenient means
 * to interact with SV service response on view.
 *
 * @author Aliaksandr_Belakurski
 */
public class StockModelHolder extends GenericService {

    private List<StockModel> modelsArray = new ArrayList<StockModel>(4);
    private StockModel favoriteStoreStock;
    private StockModel definedStoreStock;
    private StockModel nearestStoreStock;

    /**
     * @return unmodifiable view of stock models list to
     *         prohibit ability to modify it from view.
     */
    public synchronized List<StockModel> getStockModels() {
        return Collections.unmodifiableList(modelsArray);
    }

    public synchronized void clearStockModels() {
        modelsArray.clear();
        favoriteStoreStock = null;
        definedStoreStock = null;
        nearestStoreStock = null;
    }

    public synchronized void applyStockModels(Collection<StockModel> models) {
        modelsArray.addAll(models);
    }

    public synchronized StockModel getFavoriteStoreStock() {
        return favoriteStoreStock;
    }

    public synchronized void setFavoriteStoreStock(StockModel favoriteStoreStock) {
        this.favoriteStoreStock = favoriteStoreStock;
    }

    public synchronized StockModel getDefinedStoreStock() {
        return definedStoreStock;
    }

    public synchronized void setDefinedStoreStock(StockModel definedStoreStock) {
        this.definedStoreStock = definedStoreStock;
    }

    public synchronized StockModel getNearestStoreStock() {
        return nearestStoreStock;
    }

    public synchronized void setNearestStoreStock(StockModel nearestStoreStock) {
        this.nearestStoreStock = nearestStoreStock;
    }

    /**
     * @return <tt>true</tt> - in the case current user's favorite store is
     *         known.
     */
    public synchronized boolean isFavoriteStoreKnown() {
        return favoriteStoreStock != null;
    }


}