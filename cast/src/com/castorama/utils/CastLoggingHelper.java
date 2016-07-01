package com.castorama.utils;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.OrderPriceInfo;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;
import com.castorama.commerce.order.CastOrderDetails;
import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.commerce.order.CastOrderManager;

import java.math.BigDecimal;
import java.util.List;

public class CastLoggingHelper extends GenericService {

    private CastOrderManager orderManager;

    public void logOrderInfo(String orderId) {
        try {
            boolean haveOrderIssue = false;
            CastOrderImpl order = (CastOrderImpl) getOrderManager().loadOrder(orderId);
            RepositoryItem orderItem = getOrderManager().getOrderFO(orderId);
            CastOrderDetails castOrderDetails = new CastOrderDetails(orderItem, getOrderManager());

            OrderPriceInfo orderPriceInfo = order.getPriceInfo();
            StringBuilder logRecordBuffer = new StringBuilder();
            logRecordBuffer.append("Order: ").append(order)
                    .append(", amount: ").append(orderPriceInfo.getAmount())
                    .append(", tax: ").append(orderPriceInfo.getTax())
                    .append(", shipping: ").append(orderPriceInfo.getShipping())
                    .append(", total: ").append(orderPriceInfo.getTotal());
            BigDecimal orderTotal = new BigDecimal(castOrderDetails.getOrderTotal()).setScale(4, BigDecimal.ROUND_HALF_UP);
            if (isLoggingInfo()) {
                logInfo(logRecordBuffer.toString());
                logInfo("Order total: " + orderTotal.doubleValue() + ". Order price info total: " + order.getPriceInfo().getTotal());
            }
            if (orderTotal.doubleValue() != order.getPriceInfo().getTotal()) {
                if (isLoggingWarning()) {
                    logWarning("Problem with total price of order " + order.getId());
                }
                haveOrderIssue = true;
            } else {
                if (isLoggingInfo()) {
                    logInfo("Order total is correct.");
                }
            }

            @SuppressWarnings("unchecked")
            List<CommerceItem> commerceItems = order.getCommerceItems();

            if (commerceItems != null) {
                BigDecimal calculatedAmount = new BigDecimal(0.0);
                for (CommerceItem item : commerceItems) {
                    logRecordBuffer = new StringBuilder();
                    ItemPriceInfo itemPriceInfo = item.getPriceInfo();
                    calculatedAmount = calculatedAmount.add(new BigDecimal(itemPriceInfo.getAmount()));

                    logRecordBuffer.append("Commerce item: ").append(item)
                            .append(", amount: ").append(itemPriceInfo.getAmount())
                            .append(", list price: ").append(itemPriceInfo.getListPrice())
                            .append(", sale price: ").append(itemPriceInfo.getSalePrice());
                    if (isLoggingInfo()) {
                        logInfo(logRecordBuffer.toString());
                    }
                }
                double calculatedAmountAsDouble = calculatedAmount.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                if (isLoggingInfo()) {
                    logInfo("Order calculated amount: " + calculatedAmountAsDouble + ". Order price info amount: " + orderPriceInfo.getAmount());
                }
                if (calculatedAmountAsDouble != orderPriceInfo.getAmount()) {
                    if (isLoggingWarning()) {
                        logWarning("Problem with amount in order " + order.getId());
                    }
                    haveOrderIssue = true;
                } else {
                    if (isLoggingInfo()) {
                        logInfo("Order amount is correct.");
                    }
                }

            } else {
                if (isLoggingWarning()) {
                    logWarning("Order " + order.getId() + " without commerce items!");
                }
            }

            if (haveOrderIssue) {
                if (isLoggingInfo()) {
                    logInfo("Order " + order.getId() + " is not valid.");
                }
            }

        } catch (Throwable throwable) {
            if (isLoggingError()) {
                logError(throwable);
            }
        }
    }

    public CastOrderManager getOrderManager() {
        return orderManager;
    }

    public void setOrderManager(CastOrderManager orderManager) {
        this.orderManager = orderManager;
    }
}
