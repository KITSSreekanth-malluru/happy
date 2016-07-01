package com.castorama.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import com.castorama.commerce.pricing.CASTWeightShippingCalculatorImpl;

import atg.commerce.claimable.ClaimableTools;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.ShippingGroup;

import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.ShippingPriceInfo;

import atg.commerce.promotion.PromotionTools;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import atg.userdirectory.droplet.Constants;

import atg.userprofiling.Profile;

/**
 * Calculate omniture params for payment page. Calculated params:
 * <li><b>s_discountInformation</b> - Comma separated list of Discount
 * Information for each product that is ordered each item should contain
 * [discount type];[dicsount amount]. Leave an empty string if no discount is
 * applied. Make sure all discount information is sent in the SAME ORDER as the
 * s_prodList (item 1 matches product ID 1 from prodlist). Warning: this is the
 * discount already included in product prices, NOT discount coming from promo
 * codes (see further down for this type of discount)
 * <li><b>s_promoCodeUsed</b> - empty string if no promo code added, otherwise,
 * use the promo code added
 * <li><b>s_promoCodeAmount</b> - empty string if no promo code added,
 * otherwise, amount of discount applied
 * <dt>Input param:
 * <li>prodList - Comma separated list of product Ids that are ordered. Each
 * element should contain:[Product ID];[Number of items];[Total price for all
 * items of this product ID]
 * <dt>Output params:
 * <li>promoCodeUsed - omniture param s_promoCodeUsed
 * <li>promoCodeAmount - omniture param s_promoCodeAmount
 * <li>discountInfo - omniture param s_discountInformation
 *
 * @author EPAM team.
 */
public class DiscountInformationDroplet extends DynamoServlet implements Constants {
    /** PRODUCT_STD_SHIPPING_FEE property */
    private static final String PRODUCT_STD_SHIPPING_FEE = "productStdShippingFee";

    /** Coupon repository item property - promotion. */
    private static final String PROMOTION = "promotion";

    /** CLAIMED_COUPON_ID property - session property. */
    private static final String CLAIMED_COUPON_ID = "claimedCouponId";

    /** CODE_ARTICLE constant - CatalogRef repository item property. */
    private static final String CODE_ARTICLE = "CodeArticle";

    /** DISPLAY_NAME constant - promotion repository item property. */
    private static final String DISPLAY_NAME = "displayName";

    /** DISCOUNT_INFO constant - output param name. */
    private static final String DISCOUNT_INFO = "discountInfo";

    /** PROMO_CODE_USED property - output param name. */
    private static final String PROMO_CODE_USED = "promoCodeUsed";

    /** PROMO_CODE_AMOUNT property - output param name. */
    private static final String PROMO_CODE_AMOUNT = "promoCodeAmount";

    /** ORDER_DELIVERY_TYPE constant. */
    private static final String ORDER_DELIVERY_TYPE = "orderDeliveryType";

    /** CLICK_AND_COLLECT constant. */
    private static final String CLICK_AND_COLLECT = "clickAndCollect";

    /** PRODUCT_STD_SHIPPING_FEE_FOR_CNC constant. */
    private static final String PRODUCT_STD_SHIPPING_FEE_FOR_CNC = "0.00";

    /** SEMI constant */
    private static final char SEMI = ';';

    /** COMMA constant */
    private static final String COMMA = ",";

    /** PROD_LIST constant - input param. */
    private static final ParameterName PROD_LIST = ParameterName.getParameterName("prodList");

    /** mPromotionTools property */
    private PromotionTools mPromotionTools;

    /** orderHolder property. */
    private OrderHolder mOrderHolder;

    /** claimableTools property */
    private ClaimableTools mClaimableTools;

    /** mProfile property */
    private Profile mProfile;

    /** mShipmentCalculator property */
    private CASTWeightShippingCalculatorImpl mShipmentCalculator;

    /**
     * Iterates over promotions on Order and ShippingGroup levels and saves them
     * into Map
     *
     * @param  pRequest  DynamoHttpServletRequest object
     * @param  pResponse DynamoHttpServletResponse object
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        String prodList = pRequest.getParameter(PROD_LIST);

        if (StringUtils.isBlank(prodList)) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        }

        Order order = getOrderHolder().getLast();

        HashMap<String, String> refs = getsIdCatalohRefMap(order);

        HttpSession session = pRequest.getSession();

        String claimedCouponId =
            (session.getAttribute(CLAIMED_COUPON_ID) != null) ? (String) session.getAttribute(CLAIMED_COUPON_ID) : "";

        String couponPromoId = getCouponPromotionId(claimedCouponId);

        int idLength;
        StringBuffer discountInfo = new StringBuffer();

        StringBuffer productStdShippingFee = new StringBuffer();

        double promoCouponAmount = getCouponOrderDiscount(order, couponPromoId);

        String orderDeliveryType = pRequest.getParameter(ORDER_DELIVERY_TYPE);

        for (String product : prodList.split(COMMA)) {
            if (!StringUtils.isBlank(product) && ((idLength = product.indexOf(SEMI)) > 0)) {
                try {
                    CommerceItem ci = order.getCommerceItem(refs.get(product.substring(0, idLength)));

                    discountInfo.append(getProductDiscountInfo(ci, couponPromoId));

                    if (CLICK_AND_COLLECT.equals(orderDeliveryType)) {
                        productStdShippingFee.append(PRODUCT_STD_SHIPPING_FEE_FOR_CNC).append(COMMA);
                    } else {
                        productStdShippingFee.append(getShipmentCalculator().getAmount(ci)).append(COMMA);
                    }

                    promoCouponAmount += getCouponItemDiscount(ci, couponPromoId);

                } catch (CommerceItemNotFoundException e) {
                    if (isLoggingError()) {
                        logError("CommerceItemNotFoundException in DiscountInformationDroplet " + e.getMessage());
                    }
                } catch (InvalidParameterException e) {
                    if (isLoggingError()) {
                        logError("InvalidParameterException in DiscountInformationDroplet " + e.getMessage());
                    }
                }
            }  // end if
        }  // end for

        promoCouponAmount += Math.abs(getShipmentCouponPromotion(order, couponPromoId));

        if ((claimedCouponId != null) && !StringUtils.isBlank(claimedCouponId)) {
            pRequest.setParameter(PROMO_CODE_USED, claimedCouponId);
            pRequest.setParameter(PROMO_CODE_AMOUNT, Math.abs(promoCouponAmount));
        }
        String info = discountInfo.toString();

        String shippingFee = productStdShippingFee.toString();
        pRequest.setParameter(PRODUCT_STD_SHIPPING_FEE,
                              shippingFee.endsWith(COMMA) ? shippingFee.substring(0, shippingFee.length() - 1)
                                                          : shippingFee);

        pRequest.setParameter(DISCOUNT_INFO, info.endsWith(COMMA) ? info.substring(0, info.length() - 1) : info);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);

    }

    /**
     * Gets shipment promotion if applied coupon contains shipment promotion.
     *
     * @param  order         - current order.
     * @param  couponPromoId - promo id for applied coupon.
     *
     * @return shipment promotion for applied coupon
     */
    private double getShipmentCouponPromotion(Order order, String couponPromoId) {
        double shipCouponAmount = 0.0;
        if ((order != null) && !StringUtils.isBlank(couponPromoId)) {
            List<ShippingGroup> shippinggroups = order.getShippingGroups();

            for (Iterator<ShippingGroup> iterator = shippinggroups.iterator(); iterator.hasNext();) {
                ShippingPriceInfo spi = iterator.next().getPriceInfo();
                if (spi != null) {
                    for (Iterator<PricingAdjustment> it = spi.getAdjustments().iterator(); it.hasNext();) {
                        PricingAdjustment adj = it.next();
                        if (adj.getPricingModel() != null) {
                            RepositoryItem promotion = adj.getPricingModel();
                            if ((promotion != null) && promotion.getRepositoryId().equalsIgnoreCase(couponPromoId)) {
                                shipCouponAmount += adj.getTotalAdjustment();
                            }
                        }
                    }
                }
            }
        }
        return shipCouponAmount;
    }

    /**
     * Gets promotion id for coupon.
     *
     * @param  claimedCouponId - coupon id
     *
     * @return promotion id for coupon, <code>null</code> if claimedCouponId is
     *         incorrect
     */
    private String getCouponPromotionId(String claimedCouponId) {
        String couponPromoId = null;
        if (!StringUtils.isBlank(claimedCouponId)) {
            RepositoryItem coupon;
            try {
                coupon =
                    getClaimableTools().getClaimableRepository().getItem(claimedCouponId,
                                                                         getClaimableTools()
                                                                         .getCouponItemDescriptorName());
                RepositoryItem promotion = (RepositoryItem) coupon.getPropertyValue(PROMOTION);
                if (promotion != null) {
                    couponPromoId = promotion.getRepositoryId();
                }
            } catch (RepositoryException e1) {
                if (isLoggingError()) {
                    logError("RepositoryException in DiscountInformationDroplet " + e1.getMessage());
                }
            }
        }
        return couponPromoId;
    }

    /**
     * Gets discount information in format "<discount info>;<discount amount>"
     * for commerce item without coupon discount
     *
     * @param  ci            - current commerce item
     * @param  couponPromoId - promotion id from current applied coupon
     *
     * @return discount information in format "<discount info>;<discount
     *         amount>"
     */
    private StringBuffer getProductDiscountInfo(CommerceItem ci, String couponPromoId) {
        StringBuffer discountInfo = new StringBuffer();
        List<RepositoryItem> promotions = new ArrayList<RepositoryItem>();
        getPromotionTools().getItemPromotions(ci, promotions);
        if ((ci != null) && (promotions != null) && (promotions.size() > 0)) {
            double totalDiscount = getItemDiscount(ci, couponPromoId);

            for (Iterator<RepositoryItem> iterator = promotions.iterator(); iterator.hasNext();) {
                RepositoryItem promotion = iterator.next();
                if ((promotion != null) &&
                        ((!StringUtils.isBlank(couponPromoId) &&
                              !promotion.getRepositoryId().equalsIgnoreCase(couponPromoId)) ||
                             StringUtils.isBlank(couponPromoId))) {
                    discountInfo.append(promotion.getPropertyValue(DISPLAY_NAME));
                }
            }
            if (!StringUtils.isBlank(discountInfo.toString())) {
                discountInfo.append(SEMI).append(Math.abs(totalDiscount));
            }

        }
        discountInfo.append(COMMA);

        return discountInfo;
    }

    /**
     * Create map with pairs "codeArticle - commerceItemId". codeArticle - is a
     * key.
     *
     * @param  order - order with commerce items to fill the map.
     *
     * @return map with pairs "codeArticle - commerceItemId"
     */
    private HashMap<String, String> getsIdCatalohRefMap(Order order) {
        HashMap<String, String> refs = new HashMap<String, String>();
        if (order != null) {
            for (Object shippingGroup : order.getShippingGroups()) {
                for (Object ciRel : ((ShippingGroup) shippingGroup).getCommerceItemRelationships()) {
                    CommerceItem commerceItem = ((CommerceItemRelationship) ciRel).getCommerceItem();
                    RepositoryItem catalogRef = (RepositoryItem) commerceItem.getAuxiliaryData().getCatalogRef();
                    refs.put(String.valueOf(catalogRef.getPropertyValue(CODE_ARTICLE)), commerceItem.getId());
                }
            }
        }
        return refs;
    }

    /**
     * Gets item discount but without coupon discount.
     *
     * @param  ci            - commerce item
     * @param  couponPromoId - promotion id for applied coupon
     *
     * @return
     */
    private double getItemDiscount(CommerceItem ci, String couponPromoId) {
        double totalDiscount = 0.0;
        if (ci != null) {
            ItemPriceInfo priceInfo = ci.getPriceInfo();
            if (null != priceInfo) {
                double listPrice = priceInfo.getListPrice();
                double salePrice = priceInfo.getSalePrice();
                long quantity = ci.getQuantity();
                if ((salePrice != 0) && (listPrice != salePrice)) {
                    totalDiscount += (listPrice - salePrice) * quantity;
                }
                List<PricingAdjustment> adjustments = priceInfo.getAdjustments();
                if (null != adjustments) {
                    for (Iterator<PricingAdjustment> adjustmenterator = adjustments.iterator();
                             adjustmenterator.hasNext();) {
                        PricingAdjustment adjustment = adjustmenterator.next();
                        RepositoryItem promotion = adjustment.getPricingModel();
                        if ((promotion != null) &&
                                ((!StringUtils.isBlank(couponPromoId) &&
                                      !promotion.getRepositoryId().equalsIgnoreCase(couponPromoId)) ||
                                     StringUtils.isBlank(couponPromoId))) {
                            totalDiscount += Math.abs(adjustment.getTotalAdjustment());
                        }
                    }
                }
            }  // end if
        }  // end if
        return totalDiscount;
    }

    /**
     * Gets coupon discount for commerce item
     *
     * @param  ci            - commerce item
     * @param  couponPromoId - promotion id for applied coupon
     *
     * @return coupon discount for commerce item
     */
    private double getCouponItemDiscount(CommerceItem ci, String couponPromoId) {
        double totalDiscount = 0.0;
        if ((ci != null) && !StringUtils.isBlank(couponPromoId)) {
            ItemPriceInfo priceInfo = (ci != null) ? ci.getPriceInfo() : null;
            if ((null != priceInfo) && !StringUtils.isBlank(couponPromoId)) {
                List<PricingAdjustment> adjustments = priceInfo.getAdjustments();
                if (null != adjustments) {
                    for (Iterator<PricingAdjustment> adjustmenterator = adjustments.iterator();
                             adjustmenterator.hasNext();) {
                        PricingAdjustment adjustment = adjustmenterator.next();
                        RepositoryItem promotion = adjustment.getPricingModel();
                        if ((promotion != null) && promotion.getRepositoryId().equalsIgnoreCase(couponPromoId)) {
                            totalDiscount += adjustment.getTotalAdjustment();
                        }
                    }
                }
            }
        }
        return totalDiscount;
    }

    /**
     * Returns order discount only for promotion from applied coupon.
     *
     * @param  order         order
     * @param  couponPromoId applied coupon  id.
     *
     * @return coupon discount for order.
     */
    public double getCouponOrderDiscount(Order order, String couponPromoId) {
        double orderDiscount = 0.0;
        if ((order != null) && !StringUtils.isBlank(couponPromoId)) {
            OrderPriceInfo orderPriceInfo = order.getPriceInfo();
            if ((orderPriceInfo != null) && (orderPriceInfo.getAdjustments() != null)) {
                for (PricingAdjustment pa : (List<PricingAdjustment>) orderPriceInfo.getAdjustments()) {
                    RepositoryItem promotion = pa.getPricingModel();
                    if ((promotion != null) && promotion.getRepositoryId().equalsIgnoreCase(couponPromoId)) {
                        orderDiscount += pa.getTotalAdjustment();
                    }
                }
            }
        }
        return orderDiscount;
    }

    /**
     * Gets PromotionTools.
     *
     * @return the promotionTools
     */
    public PromotionTools getPromotionTools() {
        return mPromotionTools;
    }

    /**
     * Sets PromotionTools.
     *
     * @param promotionTools the promotionTools to set
     */
    public void setPromotionTools(PromotionTools promotionTools) {
        this.mPromotionTools = promotionTools;
    }

    /**
     * Gets OrderHolder.
     *
     * @return the orderHolder
     */
    public OrderHolder getOrderHolder() {
        return mOrderHolder;
    }

    /**
     * Sets OrderHolder.
     *
     * @param orderHolder the orderHolder to set
     */
    public void setOrderHolder(OrderHolder orderHolder) {
        this.mOrderHolder = orderHolder;
    }

    /**
     * Gets ClaimableTools.
     *
     * @return the claimableTools
     */
    public ClaimableTools getClaimableTools() {
        return mClaimableTools;
    }

    /**
     * Sets ClaimableTools.
     *
     * @param claimableTools the claimableTools to set
     */
    public void setClaimableTools(ClaimableTools claimableTools) {
        this.mClaimableTools = claimableTools;
    }

    /**
     * Gets Profile.
     *
     * @return the profile
     */
    public Profile getProfile() {
        return mProfile;
    }

    /**
     * Sets Profile.
     *
     * @param profile the profile to set
     */
    public void setProfile(Profile profile) {
        this.mProfile = profile;
    }

    /**
     *
     * Gets ShippingCalculator
     *
     * @return the shipmentCalculator
     */
    public CASTWeightShippingCalculatorImpl getShipmentCalculator() {
        return mShipmentCalculator;
    }

    /**
     *
     * Sets ShippingCalculator
     *
     * @param shipmentCalculator the shipmentCalculator to set
     */
    public void setShipmentCalculator(CASTWeightShippingCalculatorImpl shipmentCalculator) {
        this.mShipmentCalculator = shipmentCalculator;
    }

}
