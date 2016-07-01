package com.castorama.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;

import atg.commerce.pricing.PricingAdjustment;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * Determines which of the promotions are applicable to the given product and SKU.
 * 
 * @author Alena_Karpenkava
 */
public class CastGetApplicablePromotions extends CastPriceItem {
	/** The name of the output parameter for the applicable promotions */
	public static final String OUTPUT_PARAM_PROMOTIONS = "promotions";

	/** END_OF_USAGE property */
	private static final String END_OF_USAGE = "endUsable";

	/**
	 * Extends the based behavior to create an additional promotions output parameter.
	 * 
	 * @param pRequest -
	 *            dynamo http request
	 * @param pResponse -
	 *            dynamo http response
	 * 
	 * @throws IOException -
	 *             exception
	 * @throws ServletException -
	 *             exception
	 */
	@Override
	protected Object performPricing(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		CommerceItem item = (CommerceItem) super.performPricing(pRequest, pResponse);
		if (item != null) {
			List adjustments = item.getPriceInfo().getAdjustments();
			if (adjustments != null) {
				Iterator adjustmenterator = item.getPriceInfo().getAdjustments().iterator();
				RepositoryItem promotion;
				PricingAdjustment adjustment;
				List<RepositoryItem> promotions = new ArrayList<RepositoryItem>();

				while (adjustmenterator.hasNext()) {
					adjustment = (PricingAdjustment) adjustmenterator.next();
					promotion = adjustment.getPricingModel();
					if (promotion != null) {
						promotions.add(promotion);
					}
				}

				DateComparator compare = new DateComparator();
				Collections.sort(promotions, compare);

				pRequest.setParameter(OUTPUT_PARAM_PROMOTIONS, promotions);
			}
			return item;
		} // end if
		return null;
	}

	/**
	 * Custom comparator which compares <RepositoryItem> objects by its value's given property.
	 */
	class DateComparator implements Comparator<RepositoryItem> {
		/**
		 * Overrides the Comparator.compare() method
		 */
		public int compare(RepositoryItem one, RepositoryItem two) {
			Date oneDate = (Date) one.getPropertyValue(END_OF_USAGE);
			Date twoDate = (Date) two.getPropertyValue(END_OF_USAGE);
			if ((oneDate != null) && (twoDate != null)) {
				return oneDate.compareTo(twoDate);
			} else {
				if (oneDate == null) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}
}
