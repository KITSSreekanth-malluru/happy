package com.castorama.integration.webservice.inventory.message;

import java.util.Date;
import java.util.Set;

/**
 * @author EPAM team
 */
public class InventoryRequestMessage extends InventoryMessageBase {

    private Set<Integer> codeArticles;

    public InventoryRequestMessage(String languageCode,
                                   Date creationDateTime,
                                   int storeId,
                                   Set<Integer> codeArticles) {
        super(languageCode, creationDateTime, storeId);
        this.codeArticles = codeArticles;
    }

    public Set<Integer> getCodeArticles() {
        return codeArticles;
    }

    public void setCodeArticles(Set<Integer> codeArticles) {
        this.codeArticles = codeArticles;
    }

    @Override
    public String toString() {
        return "InventoryRequestMessage{" +
                "codeArticles=" + codeArticles +
                "} " + super.toString();
    }
}
