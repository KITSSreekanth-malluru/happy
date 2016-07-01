package com.castorama.model;

import java.util.LinkedList;
import java.util.List;

import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 *
 * @author Andrew_Logvinov
 */
public class CastoSku extends RepositoryItemWrapper {
    /** DESCRIPTOR_NAME constant. */
    public static final String DESCRIPTOR_NAME = "casto_sku";

    /** BUNDLE_LINKS constant. */
    public static final String BUNDLE_LINKS = "bundleLinks";

    /** CODE_ARTICLE constant. */
    public static final String CODE_ARTICLE = "codeArticle";
    
    /** internal field, used for export order*/
    private Double price;

    /**
     * Creates a new CastoSku object.
     *
     * @param repositoryItem
     */
    public CastoSku(RepositoryItem repositoryItem) {
        super(repositoryItem);
    }

    /**
     * Returns instance property.
     *
     * @param  repository parameter to set.
     * @param  id         parameter to set.
     *
     * @return instance property.
     *
     * @throws RepositoryException - exception
     */
    public static CastoSku getInstance(Repository repository, String id) throws RepositoryException {
    	if (null == id) return null;
    	
        RepositoryItem item = repository.getItem(id, DESCRIPTOR_NAME);

        return getInstance(item);
    }

    /**
     * Returns instance property.
     *
     * @param  skuItem parameter to set.
     *
     * @return instance property.
     */
    public static CastoSku getInstance(RepositoryItem skuItem) {
        return (null == skuItem) ? null : new CastoSku(skuItem);
    }

    /**
     * Returns bundleLinks property.
     *
     * @return bundleLinks property.
     */
    @SuppressWarnings("unchecked")
    public List<SkuLink> getBundleLinks() {
        List<SkuLink> result = null;
        List<RepositoryItem> items = (List<RepositoryItem>) repositoryItem.getPropertyValue(BUNDLE_LINKS);

        if (null != items) {
            result = new LinkedList<SkuLink>();
            for (RepositoryItem item : items) {
                result.add(SkuLink.getInstance(item));
            }
        }

        return result;
    }

    /**
     * Returns codeArticle property.
     *
     * @return codeArticle property.
     */
    public Integer getCodeArticle() {
        return (Integer) repositoryItem.getPropertyValue(CODE_ARTICLE);
    }

    /**
     * Sets the value of the codeArticle property.
     *
     * @param value parameter to set.
     */
    public void setCodeArticle(Integer value) {
        ((MutableRepositoryItem) repositoryItem).setPropertyValue(CODE_ARTICLE, value);
    }

    /**
     * @return price
     */
	public Double getPrice() {
		return price;
	}

	/**
	 * @param price 
	 */
	public void setPrice(Double price) {
		this.price = price;
	}
    
}
