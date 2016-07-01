package com.castorama.test.pages;

public class Store {
    private String mCastoramaStoreId;
    private String mATGStoreId;
    private String mLocalPriceList;
    private boolean mRetraitMagasin;
    private boolean mLocalPrix;

    public Store(String pCastoramaStoreId, String pATGStoreId, String pLocalPriceList,
                 boolean pRetraitMagasin, boolean pLocalPrix) {
        mCastoramaStoreId = pCastoramaStoreId;
        mATGStoreId = pATGStoreId;
        mLocalPriceList = pLocalPriceList;
        mRetraitMagasin = pRetraitMagasin;
        mLocalPrix = pLocalPrix;
    }

    public String getATGStoreId() {
        return mATGStoreId;
    }

    public String getLocalPriceList() {
        return mLocalPriceList;
    }

    public boolean isRetraitMagasin() {
        return mRetraitMagasin;
    }

    public boolean isLocalPrix() {
        return mLocalPrix;
    }

    public String getCastoramaStoreId() {
        return mCastoramaStoreId;
    }

    public void setCastoramaStoreId(String mCastoramaStoreId) {
        this.mCastoramaStoreId = mCastoramaStoreId;
    }
}
