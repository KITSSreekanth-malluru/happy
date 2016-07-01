package com.castorama.commerce.search.refinement;

import atg.commerce.search.refinement.CommerceFacetSearchTools;
import atg.repository.search.refinement.DisjunctionMultiValue;
import atg.repository.search.refinement.FacetDisjunctionMultiValue;
import atg.repository.search.refinement.FacetTrail;
import atg.repository.search.refinement.FacetValue;
import atg.repository.search.refinement.RangeFacetDisjunctionMultiValue;
import atg.search.routing.command.search.QueryRequest;
import atg.service.perfmonitor.PerformanceMonitor;

public class CastDocFacetSearchTools extends CommerceFacetSearchTools {
    
   

    /*
     * (non-Javadoc)
     *
     * @see atg.commerce.search.refinement.CommerceFacetSearchTools#determineCommerceRefineConfigName(atg.repository.search.refinement.FacetTrail,
     *      atg.search.routing.command.search.QueryRequest)
     */
    @Override public String determineCommerceRefineConfigName(FacetTrail pTrail, QueryRequest pRequest) {
        if (pRequest.getRefineConfig() != null) {
        	return pRequest.getRefineConfig();
        }
        return super.determineCommerceRefineConfigName(pTrail, pRequest);    	
    }


    /*
     * (non-Javadoc)
     *
     * @see atg.commerce.search.refinement.CommerceFacetSearchTools#isEligibleFacetValue(atg.repository.search.refinement.FacetTrail,
     *      atg.repository.search.refinement.FacetValue)
     */
    @Override protected boolean isEligibleFacetValue(FacetTrail pFacetTrail, FacetValue pFacetValue) {
        boolean result = true;

        if (PerformanceMonitor.isEnabled()) {
            PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.isEligibleFacetValue");
        }
        if (pFacetTrail != null && isSkipValuesAlreadyInTrail()) {
            FacetValue[] trailValues = pFacetTrail.getFacetValues();
            for (int i = 0; i < trailValues.length; i++) {
                if (trailValues[i] instanceof DisjunctionMultiValue) {
                    for (Object o : ((DisjunctionMultiValue) trailValues[i]).getValues()) {
                        Object value = pFacetValue instanceof FacetDisjunctionMultiValue ? ((FacetDisjunctionMultiValue)pFacetValue).getSingleValue() : 
                            pFacetValue instanceof RangeFacetDisjunctionMultiValue ? ((RangeFacetDisjunctionMultiValue)pFacetValue).getSingleValue() : 
                                pFacetValue.getValue();
                        if (value.equals(o)) {
                            if (PerformanceMonitor.isEnabled()) {
                                PerformanceMonitor.endOperation("search",
                                                                "TEST:CastFacetSearchTools.isEligibleFacetValue");
                            }
                            return false;
                        }
                    }

                } else {
                    result = super.isEligibleFacetValue(pFacetTrail, pFacetValue);
                    break;
                }
            }
        }  // end if
        if (PerformanceMonitor.isEnabled()) {
            PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.isEligibleFacetValue");
        }
        return result;
    }
}

