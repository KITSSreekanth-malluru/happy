/**
 *
 */
package com.castorama.mobile.services;

import atg.droplet.TagConverterManager;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;

import com.castorama.mobile.droplet.PhoneNumberConverter;

/**
 * Registers Phone Number Tag Converter
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastRegisterTagConverter extends GenericService {
    /**
     * Registers Phone Number Tag Converter.
     *
     * @throws ServiceException exception
     */
    @Override public void doStartService() throws ServiceException {
        TagConverterManager.registerTagConverter(new PhoneNumberConverter());
        super.doStartService();
    }

}
