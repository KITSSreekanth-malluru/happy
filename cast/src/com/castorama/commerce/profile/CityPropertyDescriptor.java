package com.castorama.commerce.profile;

import atg.adapter.gsa.GSAPropertyDescriptor;

import atg.repository.RepositoryItemImpl;

/**
 * Property type for field city: stores data in upper case
 *
 * @author Katsiaryna_Dmitrievich
 */
public class CityPropertyDescriptor extends GSAPropertyDescriptor {
    /** Serial version ID. */
    private static final long serialVersionUID = 4226342861085653179L;

    /**
     * Sets the property of this type for the item descriptor provided: stores
     * data in upper case.
     *
     * @param item  - repository item that is updated
     * @param value - item's value that shopuld be stored in upper case
     */
    @Override public void setPropertyValue(RepositoryItemImpl item, Object value) {
        // TODO Auto-generated method stub
        if (value instanceof String) {
            value = ((String) value).toUpperCase();
        }
        super.setPropertyValue(item, value);
    }
}
