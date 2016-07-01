package com.castorama.test.pages;

import java.util.Date;

/**
 * Author: Epam team
 * Date: 12/2/12
 * Time: 11:11 PM
 */
public class LocalStoreInfo {

    Integer storeId;
    Integer castoramaStoreId;
    String skuId;
    Integer codeArticle;
    Double localPrice;
    Boolean fPromo;
    Integer codePromo;
    Boolean clickCollectFlag;
    Boolean displayDiscount;
    Date displayDiscountBeginDate;
    Date displayDiscountEndDate;
    Double md3e;
    Double mrep;

    public Boolean getDisplayDiscount() {
        return displayDiscount;
    }

    public void setDisplayDiscount(Boolean displayDiscount) {
        this.displayDiscount = displayDiscount;
    }

    public Boolean getfPromo() {
        return fPromo;
    }

    public void setfPromo(Boolean fPromo) {
        this.fPromo = fPromo;
    }

    public Integer getCodePromo() {
        return codePromo;
    }

    public void setCodePromo(Integer codePromo) {
        this.codePromo = codePromo;
    }

    public Boolean getClickCollectFlag() {
        return clickCollectFlag;
    }

    public void setClickCollectFlag(Boolean clickCollectFlag) {
        this.clickCollectFlag = clickCollectFlag;
    }

    public Date getDisplayDiscountBeginDate() {
        return displayDiscountBeginDate;
    }

    public void setDisplayDiscountBeginDate(Date displayDiscountBeginDate) {
        this.displayDiscountBeginDate = displayDiscountBeginDate;
    }

    public Date getDisplayDiscountEndDate() {
        return displayDiscountEndDate;
    }

    public void setDisplayDiscountEndDate(Date displayDiscountEndDate) {
        this.displayDiscountEndDate = displayDiscountEndDate;
    }

    public Double getMd3e() {
        return md3e;
    }

    public void setMd3e(Double md3e) {
        this.md3e = md3e;
    }

    public Double getMrep() {
        return mrep;
    }

    public void setMrep(Double mrep) {
        this.mrep = mrep;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getCastoramaStoreId() {
        return castoramaStoreId;
    }

    public void setCastoramaStoreId(Integer castoramaStoreId) {
        this.castoramaStoreId = castoramaStoreId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public Integer getCodeArticle() {
        return codeArticle;
    }

    public void setCodeArticle(Integer codeArticle) {
        this.codeArticle = codeArticle;
    }

    public Double getLocalPrice() {
        return localPrice;
    }

    public void setLocalPrice(Double localPrice) {
        this.localPrice = localPrice;
    }
}
