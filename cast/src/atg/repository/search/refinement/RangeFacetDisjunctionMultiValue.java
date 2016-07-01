package atg.repository.search.refinement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Castorama class "overriding" for fix method toString(due to platform bug:
 * toString now returns correct string representation of multi - valued range
 * facet).
 *
 * @author Epam Team
 */
public class RangeFacetDisjunctionMultiValue extends RangeFacetValue implements DisjunctionMultiValue {
    /** Property values */
    private Object[] mValues;

    /** Property ValueSeparator */
    private String mValueSeparator = "|";

    /**
     * Constructor for RangeFacetDisjunctionMultiValue
     *
     * @param pFacet              Facet value
     * @param pValues             values of facet
     * @param pSep                separator between values
     * @param pIsLastRangeValue   boolean value, which determines if this is the
     *                            last value from available.
     * @param pLastIndicator      indicator of last value
     * @param pIndicatorSeparator String representation of separator between
     *                            last value and its indicator
     * @param pValueSeparator     separator between values of one facet
     */
    public RangeFacetDisjunctionMultiValue(Facet pFacet, Object[] pValues, String pSep, boolean pIsLastRangeValue,
                                           String pLastIndicator, String pIndicatorSeparator, String pValueSeparator) {
        super(pFacet, null, pSep, pIsLastRangeValue, pLastIndicator, pIndicatorSeparator);
        setValues(pValues);
        if (pValueSeparator != null) {
            setValueSeparator(pValueSeparator);
        }
    }

    /**
     * Returns values property.
     *
     * @return values property.
     */
    public Object[] getValues() {
        return mValues;
    }

    /**
     * Sets the value of the values property.
     *
     * @param pValues parameter to set.
     */
    public void setValues(Object[] pValues) {
        mValues = pValues;
    }

    /**
     * Returns valueSeparator property.
     *
     * @return valueSeparator property.
     */
    public String getValueSeparator() {
        return mValueSeparator;
    }

    /**
     * Sets the value of the valueSeparator property.
     *
     * @param pValueSeparator parameter to set.
     */
    public void setValueSeparator(String pValueSeparator) {
        mValueSeparator = pValueSeparator;
    }

    /**
     * Returns value property.
     *
     * @return value property.
     */
	public Object getValue() {
		return ((Object) (getValues()));
	}

    /**
     * Returns singleValue property.
     *
     * @return singleValue property.
     */
    public Object getSingleValue() {
        if (super.getValue() != null) {
            return super.getValue();
        } else {
            if (mValues != null && mValues.length > 0)
                return mValues[0];
            else
                return null;
        }
    }

    /**
     * Return correct String representation of multi-valued range facet.
     *
     * @return String representation of multi-valued range facet.
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        if ((getFacet() != null) && (getValues() != null)) {
            Object[] values = getValues();

            buffer.append(getFacet().getId());
            buffer.append(getSeparator());

            for (int i = 0; i < values.length; i++) {
                buffer.append(values[i].toString());
                if (i != (values.length - 1)) {
                    buffer.append(getValueSeparator());
                }
            }

            if (isLastRangeValue()) {
                buffer.append(mIndicatorSeparator);
                buffer.append(mLastIndicator);
            }
        }

        return buffer.toString();
    }
    
    public boolean removeValue(Object pObj) {
		if (mValues == null || mValues.length == 0 || pObj == null) {
			return false;
		}
		List valuesList = new ArrayList(Arrays.asList(mValues));
		Iterator iterator = valuesList.iterator();
		boolean found = false;
		while (iterator.hasNext()) {			
			if (pObj.equals(iterator.next())) {
				iterator.remove();
				found = true;
				break;
			}
		}
		if (found) {
			setValues(valuesList.toArray());
		}
		return found;
	}

}
