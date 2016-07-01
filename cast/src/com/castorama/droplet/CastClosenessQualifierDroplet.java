package com.castorama.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;

import atg.commerce.pricing.AmountInfo;

import atg.commerce.promotion.ClosenessQualifierDroplet;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * Shows closeness qualifiers for the given <CommerceItem> object.
 *
 * @author EPAM team
 */
public class CastClosenessQualifierDroplet extends ClosenessQualifierDroplet {
    /** COMMERCE_ITEM constant */
    public static final String COMMERCE_ITEM = "commerceItem";

    /** PMDL_RULE constant */
    public static final String PMDL_RULE = "pmdlRule";

    /** PATTERN constant */
    public static final String PATTERN = "\\<(/?)string-value\\>";

    /**
     * Compiles the closenessQualifiers requested, and renders the appropriate
     * open parameters.
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        final CommerceItem item = (CommerceItem) pRequest.getObjectParameter(COMMERCE_ITEM);
        if(item != null) {
            final String thisProdId = item.getAuxiliaryData().getProductId();
            final String thisSkuId = item.getCatalogRefId();

            List<RepositoryItem> closenessQualifiers = new ArrayList<RepositoryItem>();
            AmountInfo priceInfo = item.getPriceInfo();
            List<RepositoryItem> qualifiers = priceInfo.getClosenessQualifiers();
            if ((priceInfo != null) && (qualifiers != null)) {
                for (RepositoryItem qualifier : qualifiers) {
                    String rule = (String) (qualifier.getPropertyValue(PMDL_RULE));
                    String[] tokens = rule.split(PATTERN);
                    String itemId = tokens[1];
                    if ((thisProdId.indexOf(itemId) != -1) || (thisSkuId.indexOf(itemId) != -1)) {
                        closenessQualifiers.add(qualifier);
                    }
                }
            }
            if ((closenessQualifiers == null) || (closenessQualifiers.size() == 0)) {
                pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
            } else {
                String elementName = pRequest.getParameter(ELEMENT_NAME);
                if (elementName == null) {
                    elementName = CLOSENESS_QUALIFIERS;
                }
                pRequest.setParameter(elementName, closenessQualifiers);
                pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
            }
        } else {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        }
    }
}
