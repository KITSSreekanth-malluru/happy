package atg.repository.search.refinement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Castorama class "overriding" for fix method getValue() by adding a new method
 * getSingleValue() with old behavior.
 * 
 * @author Epam Team
 */
public class FacetDisjunctionMultiValue extends FacetValue implements DisjunctionMultiValue {
    /** Property values */
    private Object[] mValues;

    /** Property valueSeparator */
    private String mValueSeparator = "|";

    /**
     * FacetDisjunctionMultiValue constructor.
     * 
     * @param pFacet facet value
     * @param pValues values of facet
     * @param pSep separator between values
     * @param pValueSeparator separator between values of one facet
     */
    public FacetDisjunctionMultiValue(Facet pFacet, Object[] pValues, String pSep, String pValueSeparator) {
        super(pFacet, null, pSep);
        setValues(pValues);
        if (pValueSeparator != null)
            setValueSeparator(pValueSeparator);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if ((getFacet() != null) && (getValues() != null)) {
            sb.append(getFacet().getId()).append(getSeparator());
            Object[] vals = getValues();
            for (int i = 0; i < vals.length; i++) {
                sb.append(vals[i].toString());
                if (i != (vals.length - 1)) {
                    sb.append(getValueSeparator());
                }
            }
        }
        return sb.toString();
    }

    public boolean removeValue(Object pObj) {
        Object[] vals = getValues();
        if (vals != null && vals.length > 0 && pObj != null) {
            List list = new ArrayList(Arrays.asList(vals));
            for (Object o : list) {
                if (o.equals(pObj)) {
                    list.remove(o);
                    setValues(list.toArray());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns value property.
     *
     * @return value property.
     */
    public Object getValue() {
        return getValues();
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
     * Sets the value of the values property.
     *
     * @param pValues parameter to set.
     */
    public void setValues(Object[] pValues) {
        mValues = pValues;
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
     * Sets the value of the valueSeparator property.
     *
     * @param pValueSeparator parameter to set.
     */
    public void setValueSeparator(String pValueSeparator) {
        mValueSeparator = pValueSeparator;
    }

    /**
     * Returns valueSeparator property.
     *
     * @return valueSeparator property.
     */
    public String getValueSeparator() {
        return mValueSeparator;
    }
}