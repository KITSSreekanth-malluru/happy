package com.castorama.integration.webservice.inventory.message;

/**
 * @author EPAM team
 */
public class InventoryUnit {

    private Integer codeArticle;
    private long quantity;

    public InventoryUnit(Integer codeArticle, long quantity) {
        this.codeArticle = codeArticle;
        this.quantity = quantity;
    }

    public Integer getCodeArticle() {
        return codeArticle;
    }

    public void setCodeArticle(Integer codeArticle) {
        this.codeArticle = codeArticle;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}
